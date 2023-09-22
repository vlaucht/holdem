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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Should_ReturnUserExtra_If_ItExists() {
        String username = "testUser";
        UserExtra mockUserExtra = new UserExtra(username);
        when(userExtraRepository.findById(username)).thenReturn(Optional.of(mockUserExtra));

        UserExtra result = userExtraService.getUserExtra(username);

        assertEquals(mockUserExtra, result);
        verify(userExtraRepository, times(1)).findById(username);
    }

    @Test
    void Should_CreateUserExtra_If_ItDoesntExist() {
        userExtraService.initialBankroll = initialBankroll;
        String username = "testUser";
        when(userExtraRepository.findById(username)).thenReturn(Optional.empty());
        when(avatarService.getRandomAvatarUrl()).thenReturn("mockedAvatarUrl");
        when(userExtraRepository.save(any(UserExtra.class))).thenAnswer(i -> i.getArguments()[0]);
        UserExtra result = userExtraService.getUserExtra(username);

        assertEquals(username, result.getUsername());
        assertEquals("mockedAvatarUrl", result.getAvatar());
        assertEquals(new BigInteger(initialBankroll), result.getBankroll());
        verify(userExtraRepository, times(1)).findById(username);
        verify(avatarService, times(1)).getRandomAvatarUrl();
        verify(userExtraRepository, times(1)).save(result);
    }

    @Test
    void Should_SaveUserExtraToDatabase() {
        UserExtra mockUserExtra = new UserExtra("testUser");
        when(userExtraRepository.save(mockUserExtra)).thenReturn(mockUserExtra);

        UserExtra result = userExtraService.saveUserExtra(mockUserExtra);

        assertEquals(mockUserExtra, result);
        verify(userExtraRepository, times(1)).save(mockUserExtra);
    }

    @Test
    void Should_RestoreBankrollToInitialValue() {
        userExtraService.initialBankroll = initialBankroll;
        String username = "testUser";
        UserExtra mockUserExtra = new UserExtra(username);
        when(userExtraRepository.findById(username)).thenReturn(Optional.of(mockUserExtra));
        when(userExtraRepository.save(mockUserExtra)).thenReturn(mockUserExtra);

        UserExtra result = userExtraService.recharge(username);

        assertEquals(new BigInteger(initialBankroll), result.getBankroll());
        verify(userExtraRepository, times(1)).findById(username);
        verify(userExtraRepository, times(1)).save(mockUserExtra);
    }

}