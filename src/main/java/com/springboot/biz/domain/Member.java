package com.springboot.biz.domain;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "memberRoleList")
public class Member {   //회원정보와 함께 권한 목록을 가질 수 있도록 설계

    @Id
    private String email;
    private String nickname;
    private String pw;
    private boolean social;

    @ElementCollection(fetch = FetchType.LAZY)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    public void addRole(MemberRole memberRole) {
        memberRoleList.add(memberRole);
    }
    public void clearRole(){
        memberRoleList.clear();
    }
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }
    public void changePw(String pw) {
        this.pw = pw;
    }
    public void changeSocial(boolean social) {
        this.social = social;
    }
}
