package de.thm.holdem.service;

import de.thm.holdem.model.user.UserExtra;
import de.thm.holdem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigInteger;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userExtraService;

    @Mock
    private UserRepository userExtraRepository;

    @Mock
    private AvatarService avatarService;

    private final String initialBankroll = "10000";

    private final String id = "test::id";
    private final String username = "test::username";

    private UserExtra userExtra;
    @BeforeEach
    void setUp() {
        userExtra = new UserExtra(id, username);
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void Should_ReturnUserExtra_If_ItExists() {
        when(userExtraRepository.findById(id)).thenReturn(Optional.of(userExtra));

        UserExtra result = userExtraService.getUserExtra(id);

        assertEquals(userExtra, result);
        verify(userExtraRepository, times(1)).findById(id);
    }

    @Test
    void Should_CreateUserExtra_If_ItDoesntExist() {
        userExtraService.initialBankroll = initialBankroll;
        when(userExtraRepository.findById(id)).thenReturn(Optional.empty());
        when(avatarService.getRandomAvatarUrl()).thenReturn("mockedAvatarUrl");
        when(userExtraRepository.save(any(UserExtra.class))).thenAnswer(i -> i.getArguments()[0]);
        UserExtra result = userExtraService.getUserExtra(id, username);

        assertEquals(id, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals("mockedAvatarUrl", result.getAvatar());
        assertEquals(new BigInteger(initialBankroll), result.getBankroll());
        verify(userExtraRepository, times(1)).findById(id);
        verify(avatarService, times(1)).getRandomAvatarUrl();
        verify(userExtraRepository, times(1)).save(result);
    }

    @Test
    void Should_SaveUserExtraToDatabase() {
        when(userExtraRepository.save(userExtra)).thenReturn(userExtra);

        UserExtra result = userExtraService.saveUserExtra(userExtra);

        assertEquals(userExtra, result);
        verify(userExtraRepository, times(1)).save(userExtra);
    }

    @Test
    void Should_RestoreBankrollToInitialValue() {
        userExtraService.initialBankroll = initialBankroll;

        when(userExtraRepository.findById(id)).thenReturn(Optional.of(userExtra));
        when(userExtraRepository.save(userExtra)).thenReturn(userExtra);

        UserExtra result = userExtraService.recharge(id);

        assertEquals(new BigInteger(initialBankroll), result.getBankroll());
        verify(userExtraRepository, times(1)).findById(id);
        verify(userExtraRepository, times(1)).save(userExtra);
    }

}