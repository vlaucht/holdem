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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "testUser")
    void Should_GetUserExtra() throws Exception {
        UserExtra mockUserExtra = new UserExtra("testUser");
        mockUserExtra.setBankroll(1000);
        when(userExtraService.getUserExtra("testUser")).thenReturn(mockUserExtra);

        ResultActions result = mockMvc.perform(get("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.bankroll").value(1000));
    }

    @Test
    @WithMockUser(username = "testUser")
    void Should_RechargeUserBankroll() throws Exception {
        UserExtra mockUserExtra = new UserExtra("testUser");
        mockUserExtra.setBankroll(1000);
        when(userExtraService.recharge("testUser")).thenReturn(mockUserExtra);

        ResultActions result = mockMvc.perform(post("/api/user/me")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(MockMvcResultMatchers.jsonPath("$.bankroll").value(1000));
    }

    @Test
    void Should_BlockRequest_If_NotAuthorized() throws Exception {
        mockMvc.perform(get("/api/user/me"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


}