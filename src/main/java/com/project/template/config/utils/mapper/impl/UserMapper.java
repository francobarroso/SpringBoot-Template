package com.project.template.config.utils.mapper.impl;

import com.project.template.config.utils.mapper.IMapper;
import com.project.template.domain.dto.UserDTO;
import com.project.template.domain.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends IMapper<UserEntity, UserDTO> {
    @Override
    UserDTO toDTO(UserEntity source);

    @Override
    UserEntity toEntity(UserDTO source);
}
