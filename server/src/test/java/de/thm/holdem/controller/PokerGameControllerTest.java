package de.thm.holdem.controller;

import de.thm.holdem.Application;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.game.poker.TableType;
import de.thm.holdem.security.JwtAuthConverter;
import de.thm.holdem.service.PokerGameService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PokerGameController.class)
@ContextConfiguration(classes = {Application.class, PokerGameService.class})
class PokerGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokerGameService pokerGameService;

    @MockBean
    private JwtAuthConverter jwtAuthConverter;

    @InjectMocks
    private PokerGameController pokerGameController;

    private PokerGame mockGame;

    private PokerGameCreateRequest createRequest;

    private final ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockGame = mock(PokerGame.class);
        createRequest = new PokerGameCreateRequest("My Poker Game", 500, TableType.FIXED_LIMIT, 5);
    }

    @Test
    @WithMockUser(username = "testUser")
    void Should_CreatePokerGame_If_RequestIsValid() throws Exception {
        String requestJson =  mapper.writeValueAsString(createRequest);
        requestJson = requestJson.replace("Fixed-Limit", "FL");
        when(pokerGameService.createGame("testUser", createRequest)).thenReturn(mockGame);
        mockMvc.perform(post("/create")
                        .content(requestJson)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(pokerGameService, times(1)).createGame("testUser", createRequest);
    }

    @Test
    @WithMockUser(username = "testUser")
    void Should_ReturnException_If_CreateRequestIsInvalid() throws Exception {
        createRequest.setName("");
        String requestJson =  mapper.writeValueAsString(createRequest);
        requestJson = requestJson.replace("Fixed-Limit", "FL");
        mockMvc.perform(post("/create")
                                .content(requestJson)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(pokerGameService, times(0)).createGame("testUser", createRequest);
    }


}