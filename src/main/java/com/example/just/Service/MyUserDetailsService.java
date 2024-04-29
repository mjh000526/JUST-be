package com.example.just.Service;

import com.example.just.Dao.Member;
import com.example.just.Repository.MemberRepository;
import com.example.just.auth.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {// 유저 정보를 가져오는 서비스

    @Autowired
    private MemberRepository memberRepository;
    @Override //유저정보를 가져오는 메서드
    public UserDetails loadUserByUsername(String Id) throws UsernameNotFoundException {
        Long member_Id = Long.parseLong(Id);
        Member member = memberRepository.findById(member_Id).get();
        if(member==null){
            return null;
        }
        return new PrincipalDetails(member);
    }
}
