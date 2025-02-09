package com.springboot.biz;

import com.springboot.biz.domain.Member;
import com.springboot.biz.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /*@Test
    public void test(){

        for(int i=0;i<10;i++){
            Member member = Member.builder()
                    .email("user" + i + "@aaa.com")
                    .pw(passwordEncoder.encode("1111"))
                    .nickname("USER" + i )
                    .build();
            member.addRole(MemberRole.USER);
            if(i>=5 ){
                member.addRole(MemberRole.MANAGER);
            }
            if(i >= 8){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        }
    }*/

    @Test
    public void testRead(){
        String email = "user0@aaa.com";
        Member member = memberRepository.getWithRoles(email);
        System.out.println("내가 검색한 객체 : " + member);
    }
}
