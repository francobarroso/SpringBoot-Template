package com.project.template.services.auth;

import com.project.template.config.utils.jwt.JwtUtils;
import com.project.template.config.utils.mapper.impl.UserMapper;
import com.project.template.domain.dto.*;
import com.project.template.domain.entities.Role;
import com.project.template.domain.entities.UserEntity;
import com.project.template.domain.entities.UserHistory;
import com.project.template.repositories.RoleRepository;
import com.project.template.repositories.UserHistoryRepository;
import com.project.template.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    UserRepository userRepository;
    UserHistoryRepository userHistoryRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    JwtUtils jwtUtils;
    PasswordEncoder passwordEncoder;

    public UserDetailServiceImpl(UserRepository userRepository, UserHistoryRepository userHistoryRepository, RoleRepository roleRepository, UserMapper userMapper, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
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

        return new User(userDb.getUsername(),
                userDb.getPassword(),
                userHistoryDb.isEnabled(),
                userHistoryDb.isAccountNoExpired(),
                userHistoryDb.isCredentialNoExpired(),
                userHistoryDb.isAccountNoLocked(),
                authorityList);
    }

    public Response<AuthResponse> loginUser(AuthLoginRequest authLoginRequest){
        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, accessToken);

        return new Response<>(true,authResponse,"User Logged successfully");
    }

    public Authentication authenticate(String username, String password){
        UserDetails userDetails = this.loadUserByUsername(username);

        if(userDetails == null){
            throw new BadCredentialsException("Invalid username or password");
        }

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }

    public Response<AuthResponse> register(UserDTO user) {
        Optional<UserEntity> userDb = userRepository.findByUsername(user.getUsername());
        if (userDb.isPresent()) {
            return new Response<>(false,null,"User already exist");
        }

        List<String> rolesRequest = new ArrayList<>();
        user.getRoles()
                .forEach(role -> rolesRequest.add(role.getRoleName().name()));

        Set<Role> rolesDb = roleRepository.findRolesByRoleNameIn(rolesRequest);
        if(rolesDb.size() != user.getRoles().size()){
            return new Response<>(false,null,"The roles specified doesn't exist");
        }

        UserEntity userEntity = userMapper.toEntity(user);
        UserHistory userHistory = UserHistory.builder()
                .user(userEntity)
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setUserHistory(userHistory);

        UserEntity userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        userCreated.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleName().name()))));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(), userCreated.getPassword(), authorityList);

        String accessToken = jwtUtils.createToken(authentication);
        AuthResponse authResponse = new AuthResponse(userCreated.getUsername(), accessToken);

        return new Response<>(true,authResponse,"User created successfully");
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}
