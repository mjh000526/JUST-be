package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Dto.MemberDto;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class Details extends User {

    public Details(Member member) {
        super(member.getId().toString(), member.getEmail(),
                AuthorityUtils.createAuthorityList(String.valueOf(member.getRole())));
    }

}
