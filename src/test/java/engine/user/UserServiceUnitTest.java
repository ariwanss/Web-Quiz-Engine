package engine.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    User user;

    void initUser() {
        user = new User("someone@gmail.com", "password123");
        user.setId(1);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initUser();
    }

    @Test
    void register() {
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        userService.register(user);
        Mockito.verify(userRepository).save(userArgumentCaptor.capture());

        assertEquals(user, userArgumentCaptor.getValue());
    }

    @Test
    void registerUserAlreadyExists() {
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.register(user),
                HttpStatus.BAD_REQUEST.getReasonPhrase());
    }
}