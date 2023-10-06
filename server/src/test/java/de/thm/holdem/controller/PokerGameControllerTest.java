package de.thm.holdem.controller;

import de.thm.holdem.Application;
import de.thm.holdem.dto.PokerGameCreateRequest;
import de.thm.holdem.dto.PokerGameStateDto;
import de.thm.holdem.model.game.poker.PokerGame;
import de.thm.holdem.model.game.poker.TableType;
import de.thm.holdem.security.JwtAuthConverter;
import de.thm.holdem.service.ConnectionRegistry;
import de.thm.holdem.service.PokerGameService;
import de.thm.holdem.service.WebsocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(PokerGameController.class)
@ContextConfiguration(classes = {Application.class, PokerGameService.class})
class PokerGameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokerGameService pokerGameService;

    @MockBean
    private ConnectionRegistry registry;

    @MockBean
    private WebsocketService websocketService;

    @MockBean
    private JwtAuthConverter jwtAuthConverter;

    @InjectMocks
    private PokerGameController pokerGameController;

    private PokerGame mockGame;

    private PokerGameCreateRequest createRequest;

    private final ObjectMapper mapper = new ObjectMapper();
    private Jwt jwt;

    private final String id = "test::id";
    private final String username = "test::username";


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockGame = mock(PokerGame.class);
        createRequest = new PokerGameCreateRequest("My Poker Game", 500, TableType.FIXED_LIMIT, 5);
        jwt = Jwt.withTokenValue("test::token")
                .claim("sub", id)
                .claim("preferred_username", username)
                .header("alg", "RS256")
                .build();
    }

    @Test
    void Should_CreatePokerGame_If_RequestIsValid() throws Exception {
        String requestJson =  mapper.writeValueAsString(createRequest);
        requestJson = requestJson.replace("Fixed-Limit", "FL");
        when(pokerGameService.createGame(id, createRequest)).thenReturn(mockGame);
        try {
            MockedStatic<PokerGameStateDto> dto = mockStatic(PokerGameStateDto.class);
            dto.when(() -> PokerGameStateDto.from(mockGame)).thenReturn(new PokerGameStateDto());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mockMvc.perform(post("/api/poker/create")
                        .content(requestJson)
                        .with(csrf())
                        .with(jwt().jwt(jwt))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(pokerGameService, times(1)).createGame(id, createRequest);
    }

    @Test
    void Should_ReturnException_If_CreateRequestIsInvalid() throws Exception {
        createRequest.setName("");
        String requestJson =  mapper.writeValueAsString(createRequest);
        requestJson = requestJson.replace("Fixed-Limit", "FL");
        mockMvc.perform(post("/api/poker/create")
                                .content(requestJson)
                                .with(csrf())
                                .with(jwt().jwt(jwt))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(pokerGameService, times(0)).createGame(username, createRequest);
    }


}