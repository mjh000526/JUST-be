package com.example.just.Mapper;

import com.example.just.Dao.Post;
import com.example.just.Dto.PutPostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PutPostDto, Post>{
}
