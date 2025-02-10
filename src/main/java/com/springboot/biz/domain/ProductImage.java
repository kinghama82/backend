package com.springboot.biz.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable  //값 타입 객체  (내장객체) - 엔티티안에 속해있는 부수적인 데이터를 표현할 때 사용
//JPA 엔티티 클래스에 내장 타입을 사용하기 위해 지정하는 어노테이션
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {
    //상품등록 핵심은 상품 자체, 파일들은 상품을 설명하는 부수적인 데이터임
    //상품은 PK를 가지는 완전한 엔티티로 작성
    //파일들은 엔티티에 속해 있는 데이터 따로 만들면 조인하느라 속도가 느려짐

    private String fileName;

    //이미지에 붙이는 번호, 상품 목록을 출력할 때 ord값이 0번인 이미지를 대표 이미지로 사용할 수 있음
    private int ord;

    public void setOrd(int ord){
        this.ord = ord;
    }
}
