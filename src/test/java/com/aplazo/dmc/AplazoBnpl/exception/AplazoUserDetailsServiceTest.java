package com.aplazo.dmc.AplazoBnpl.exception;

import com.aplazo.dmc.AplazoBnpl.security.AplazoUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class AplazoUserDetailsServiceTest {

    private AplazoUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new AplazoUserDetailsService();
    }

    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUsernameIsValid() {
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
        assertEquals("{noop}password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUsernameIsInvalid() {
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("invalidUser");
        });
    }
}
