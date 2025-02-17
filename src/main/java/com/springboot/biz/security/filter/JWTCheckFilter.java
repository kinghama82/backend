package com.springboot.biz.security.filter;

import com.google.gson.Gson;
import com.springboot.biz.dto.MemberDTO;
import com.springboot.biz.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("-----------JWTCheckFilter----------");

        String authHeaderStr = request.getHeader("Authorization");
        //bearer 문자와 뒤에 띄어쓰기 되어있는 한글자 총7글자를 잘라내고 나머지를 사용함
        try {
        	//bearer accesstoken...
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("JWT claims : " + claims);
            //filterChain.doFilter(request, response);
            
            String email = (String) claims.get("email");
            String pw = (String) claims.get("pw");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean)claims.get("social");
            List<String> roleNames = (List<String>)claims.get("roleNames");
            
            MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames);
            
            log.info("------------------");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());
            
            UsernamePasswordAuthenticationToken authenticationToken =
            		new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());
            
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //성공했다면 다음으로 이동하세요~의 의미
            filterChain.doFilter(request, response);
        }catch (Exception e) {
            log.error("JWT Check Error........");
            log.error(e.getMessage());

            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json;charset=utf-8");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
    
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		//preflight요청은 체크하지 않음
    	if(request.getMethod().equals("OPTIONs")) {
			return true;
		}
		String path = request.getRequestURI();
		log.info("체크 url : " + path);
		
		//api/member/ 경로의 호출은 체크하지 않음
		if(path.startsWith("/api/member/")) {
			return true;
		}
		
		//이미지 조회 경로는 체크하지 않는다면
		if(path.startsWith("/api/products/view/")) {
			return true;
		}
		//flase 체크할거야  true 체크 안할거야
		return false;
	}
}
