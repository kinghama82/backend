package com.springboot.biz.security.filter;

import com.google.gson.Gson;
import com.springboot.biz.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.info("-----------JWTCheckFilter----------");

        String authHeaderStr = request.getHeader("Authorization");
        try {
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("JWT claims : " + claims);
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
}
