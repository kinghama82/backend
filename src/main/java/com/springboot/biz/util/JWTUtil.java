package com.springboot.biz.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import javax.crypto.SecretKey;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Log4j2
public class JWTUtil {   //문자열 생성시 필요한 암호키를 지정(길이가 짧으면 문제생김 -> 30자이상의 문자열로 암호키 지정
                         //특수기호는 들어가면 안됨 숫자,영문자만 가능
    private static String key = "1234567890123456789012345678901234567890";

    //jwt 문자열 생성 메소드 -> 인증된 사용자에게 jwt토큰을 만들어주기 위한 메소드
    public static String generateToken(Map<String, Object> valueMap, int min) {

        SecretKey key = null;
                     //hash-based message authentication code : 해시기반 메세지 검증 코드 -> 해시함수 이용한 암호화 인증 기술
                     //얘는 공개키 암호화 방식 사용
                     //대칭키(비밀키) 암호화방식 / 비대칭키(공개키) 암호화방식
        try {        //hmacShaKeyFor -> hmac 알고리즘을 적용한 key(자바시큐리티.key가 갖고있음) 객체 생성
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        // 사용자가 인증요청 -> 서버는 요청이 들어온 id/pw 확인
        // 사용자가 맞다면 서버가 access토큰 / 리프레시토큰 발행해서 사용자에게 보내줌
        // 사용자는 요청(get/post등)시 헤더에 토큰을 넣어서 같이 보냄
        // 서버는 사용자가 보내온 토큰을 검증 액세스토큰 만료시간 확인(시간지난 토큰은 유효한 토큰이 아니기때문)
        // 만약 만료시간이 지나서 사용자가 요청시 서버는 리프레시 토큰을 이용해서 액세스토큰을 재발급 해서 사용자에게 다시 줌
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ","JWT"))
                .setClaims(valueMap)  //인증된 사용자와 관련된 정보를 저장
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) //jwt 발행 일자 설정 java.util
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) //jwt 만료일자
                .signWith(key) //서명을 위한 key 객체를 넣어줌(에: 전자서명)
                .compact(); //jwt를 생성하고 직렬화 (build는 객체만 생성 / compact는 생성 + 직렬화)
        return jwtStr;
    }

    //검증을 위한 메소드
    public static Map<String, Object> validateToken(String token) {
        Map<String, Object> claim = null;

        try {
            SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            claim = Jwts.parserBuilder()   //토큰의 유효성을 검사할 객체 생성
                    .setSigningKey(key)    //서명키로 설정
                    .build()
                    .parseClaimsJws(token) //토큰의 유효성 검사하는 메소드
                    .getBody();
            //jwt 헤더 + 페이로드 + 서명
            //헤더 : 암호화 알고리즘, 토큰의 정보
            //페이로드 : 사용자 정보, 토큰의 만료시간 등의 정보 담김
            //서명 : 헤더와 페이로드 암호화
        }catch (MalformedJwtException malformedJwtException){
            throw  new CustomJWTException("MalFormed");  //유효하지 않은 토큰 찾기
        }catch (ExpiredJwtException expiredJwtException){
            throw  new CustomJWTException("Expired");  // 만료된 토큰 찾기
        }catch (InvalidClaimException invalidClaimException){
            throw new CustomJWTException("Invalid");  // 형식에 맞지 않는 토큰
        }catch (JwtException jwtException){
            throw new CustomJWTException("JWTError");
        }catch (Exception e){
            throw new CustomJWTException("Error");
        }
        return claim;
    }

}
