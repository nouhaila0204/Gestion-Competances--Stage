package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.config.JwtService;
import com.sbgs.backend_2026.dto.Auth.LoginRequest;
import com.sbgs.backend_2026.dto.Auth.LoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${application.security.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${application.security.jwt.expiration-ms:86400000}")
    private long expirationMs;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        String token = jwtService.generateToken(userDetails);
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        response.addHeader(HttpHeaders.SET_COOKIE, construireCookie(token, expirationMs / 1000).toString());

        return new LoginResponse(userDetails.getUsername(), role);
    }

    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        // maxAge=0 -- demande au navigateur de supprimer immediatement le cookie
        response.addHeader(HttpHeaders.SET_COOKIE, construireCookie("", 0).toString());
    }

    private ResponseCookie construireCookie(String valeur, long maxAgeSecondes) {
        return ResponseCookie.from("jwt", valeur)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSecondes)
                .build();
    }
}