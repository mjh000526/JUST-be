package com.example.just.auth;

import com.example.just.Dao.Member;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
@ToString
public class PrincipalDetails implements UserDetails, OAuth2User {
    private Member member;
    private Map<String,Object> attributes;
    public PrincipalDetails(Member member){
        this.member = member;
    }

    public PrincipalDetails(Member member, Map<String,Object> attributes){
        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //UserDetails 구현
    //유저의 권한 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                //임의대로 변경했음
                return member.getRole().toString();
            }
        });
        return collect;
    }

    //비밀번호 구현
    @Override
    public String getPassword() {
        return member.getEmail();
    }

    //PK값 리턴
    @Override
    public String getUsername() {
        return member.getId().toString();
    }


    //계정 만료여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //계정 잠김여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //계정 비번만료여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //계정 활성화 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        String sub = attributes.get("sub").toString();
        return sub;
    }
}
