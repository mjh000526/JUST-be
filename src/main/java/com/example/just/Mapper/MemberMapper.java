package com.example.just.Mapper;


import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper extends EntityMapper <MemberDto, Member>{
}
