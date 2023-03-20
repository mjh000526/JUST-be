package com.example.just.Mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

public interface EntityMapper <D, E>{
    E toEntity (D dto);
    D toDto (E entity);
}