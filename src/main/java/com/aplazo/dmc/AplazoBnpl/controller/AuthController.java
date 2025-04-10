package com.aplazo.dmc.AplazoBnpl.controller;

import com.aplazo.dmc.AplazoBnpl.controller.dto.request.AuthRequest;
import com.aplazo.dmc.AplazoBnpl.security.AplazoUserDetailsService;
import com.aplazo.dmc.AplazoBnpl.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final AplazoUserDetailsService aplazoUserDetailsService;

    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            AplazoUserDetailsService aplazoUserDetailsService,
            JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.aplazoUserDetailsService = aplazoUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        final UserDetails userDetails = aplazoUserDetailsService.loadUserByUsername(authRequest.getUsername());
        return jwtUtil.generateToken(userDetails.getUsername());
    }
}
