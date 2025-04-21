package com.example.twitterclone.config

import com.example.twitterclone.service.security.JwtService
import groovy.util.logging.Slf4j
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@Component
class JwtAuthFilter extends OncePerRequestFilter {

    final JwtService jwtService

    JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        def authHeader = request.getHeader("Authorization")

        if (!authHeader?.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        def token = authHeader.substring(7)

        try {
            def userId = jwtService.extractUserId(token)
            log.info("JWT Filter: extracted userId = ${userId}")

            def authorities = [new SimpleGrantedAuthority("ROLE_USER")]
            def auth = new UsernamePasswordAuthenticationToken(userId, null, authorities)
            SecurityContextHolder.context.authentication = auth

        } catch (Exception ex) {
            log.error("JWT Filter: token error", ex)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
            return
        }

        filterChain.doFilter(request, response)
    }
}
