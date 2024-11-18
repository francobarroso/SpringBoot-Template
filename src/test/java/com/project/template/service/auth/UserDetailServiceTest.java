package com.project.template.service.auth;

import com.project.template.domain.dto.Response;
import com.project.template.mock.UserDetailServiceProvider;
import com.project.template.config.utils.jwt.JwtUtils;
import com.project.template.config.utils.mapper.impl.UserMapper;
import com.project.template.domain.dto.AuthResponse;
import com.project.template.domain.dto.UserDTO;
import com.project.template.domain.entities.UserEntity;
import com.project.template.repositories.RoleRepository;
import com.project.template.repositories.UserRepository;
import com.project.template.services.auth.UserDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void testRegisterSuccessfully(){
        //Given
        UserDTO userDTO = UserDetailServiceProvider.userDtoMock();
        UserEntity userMock = UserDetailServiceProvider.userMock();

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(roleRepository.findRolesByRoleNameIn(UserDetailServiceProvider.rolesString())).thenReturn(UserDetailServiceProvider.rolesMock());
        when(userMapper.toEntity(userDTO)).thenReturn(userMock);
        when(passwordEncoder.encode(UserDetailServiceProvider.userMock().getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtils.createToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("mockedToken");

        //When
        Response<AuthResponse> response = userDetailService.register(userDTO);

        //Then
        assertNotNull(response);
        assertEquals("test", response.data().username());
        assertEquals("User created successfully", response.message());
        assertEquals("mockedToken", response.data().token());
        assertTrue(response.status());

        //Verify interactions
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(passwordEncoder).encode(userDTO.getPassword());
        verify(roleRepository).findRolesByRoleNameIn(UserDetailServiceProvider.rolesString());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        //Given
        UserDTO userDTO = UserDetailServiceProvider.userDtoMock();
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(UserDetailServiceProvider.userMock()));

        //When
        Response<AuthResponse> response = userDetailService.register(userDTO);

        //Then
        assertFalse(response.status());
        assertNull(response.data());
        assertEquals("User already exist", response.message());

        //Verify interactions
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testRegisterInvalidRoles() {
        // Given
        UserDTO userDTO = UserDetailServiceProvider.userDtoMock();
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(roleRepository.findRolesByRoleNameIn(UserDetailServiceProvider.rolesString())).thenReturn(Collections.emptySet());

        //When
        Response<AuthResponse> response = userDetailService.register(userDTO);

        //Then
        assertFalse(response.status());
        assertNull(response.data());
        assertEquals("The roles specified doesn't exist", response.message());

        //Verify interactions
        verify(userRepository, never()).save(any(UserEntity.class));
    }
}
