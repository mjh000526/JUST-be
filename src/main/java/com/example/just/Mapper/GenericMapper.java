package com.example.just.Mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

public interface GenericMapper <D, E>{
    D toDto(E e);
    E toEntity(D d);

    List<D> toDoList(List<E> entityList);
    List<E> toEntity(List<D> dtoList);

}