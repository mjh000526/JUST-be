package com.example.just.Mapper;

import com.example.just.Dao.Post;
import com.example.just.Dto.PostDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PostDto, Post>{
}
