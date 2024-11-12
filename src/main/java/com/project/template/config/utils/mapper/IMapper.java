package com.project.template.config.utils.mapper;

import java.util.List;

public interface IMapper<E, D> {
    D toDTO(E source);
    E toEntity(D source);
    List<D> toDTOsList(List<E> source);
}
