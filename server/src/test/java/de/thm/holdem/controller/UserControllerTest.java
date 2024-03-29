package de.thm.holdem.controller;

import de.thm.holdem.Application;
import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.security.JwtAuthConverter;
import de.thm.holdem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigInteger;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {Application.class, UserService.class})
class UserControllerTest {

    @MockBean
    private UserService userExtraService;

    @MockBean
    private JwtAuthConverter jwtAuthConverter;

    @InjectMocks
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    private final String id = "test::id";
    private final String username = "test::username";
    private UserExtra userExtra;

    private Jwt jwt;


    @BeforeEach
    void setup() {
        userExtra = new UserExtra(id, username);
        userExtra.setBankroll(BigInteger.valueOf(1000));
        MockitoAnnotations.openMocks(this);
        jwt = Jwt.withTokenValue("test::token")
                .claim("sub", id)
                .claim("preferred_username", username)
                .header("alg", "RS256")
                .build();
    }

    @Test
    void Should_GetUserExtra() throws Exception {
        when(userExtraService.getUserExtra(id, username)).thenReturn(userExtra);

        ResultActions result = mockMvc.perform(get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(jsonPath("$.bankroll").value(1000));
        verify(userExtraService, times(1)).getUserExtra(id, username);
    }

    @Test
    void Should_RechargeUserBankroll() throws Exception {
        when(userExtraService.recharge(id)).thenReturn(userExtra);

        ResultActions result = mockMvc.perform(post("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf())
                        .with(jwt().jwt(jwt)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(jsonPath("$.bankroll").value(1000));
        verify(userExtraService, times(1)).recharge(id);
    }

    @Test
    void Should_BlockRequest_If_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(status().isUnauthorized());
    }


}