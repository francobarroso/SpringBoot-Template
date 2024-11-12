package com.project.template.services.impl;

import com.project.template.config.utils.mapper.impl.UserMapper;
import com.project.template.domain.dto.UserDTO;
import com.project.template.domain.entities.UserEntity;
import com.project.template.domain.entities.UserHistory;
import com.project.template.repositories.UserHistoryRepository;
import com.project.template.repositories.UserRepository;
import com.project.template.services.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
     UserRepository userRepository;
     UserHistoryRepository userHistoryRepository;
     PasswordEncoder passwordEncoder;
     UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper, UserHistoryRepository userHistoryRepository){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userHistoryRepository = userHistoryRepository;
    }

    @Override
    public UserDTO register(UserDTO user) {
        Optional<UserEntity> userDb = userRepository.findByUsername(user.getUsername());
        if (userDb.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity userEntity = userMapper.toEntity(user);
        UserHistory userHistory = new UserHistory();
        userHistory.setUser(userEntity);
        userHistory.setEnabled(true);
        userHistory.setAccountNoExpired(true);
        userHistory.setAccountNoLocked(true);
        userHistory.setCredentialNoExpired(true);

        userEntity.setUserHistory(userHistory);

        userRepository.save(userEntity);

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userDb = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist"));

        UserHistory userHistoryDb = userHistoryRepository.findByUser(userDb.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User History doesn't exist"));

        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userDb.getRoles()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleName().name()))));

        userDb.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        return new User (userDb.getUsername(),
                userDb.getPassword(),
                userHistoryDb.isEnabled(),
                userHistoryDb.isAccountNoExpired(),
                userHistoryDb.isCredentialNoExpired(),
                userHistoryDb.isAccountNoLocked(),
                authorityList);
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}
