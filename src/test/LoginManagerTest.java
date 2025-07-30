package test;

import main.Database;
import main.LoginManager;
import main.Student;
import main.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class LoginManagerTest {

    @Mock
    private Database mockDb;

    private LoginManager loginManager;

    @BeforeEach
    void setUp() {
        loginManager = new LoginManager(mockDb); // Mock injected automatically
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        User mockUser = mock(User.class);
        when(mockDb.authenticate("test@example.com", 1234)).thenReturn(mockUser);

        User result = loginManager.authenticate("test@example.com", 1234);
        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(mockDb).authenticate("test@example.com", 1234);
    }

    @Test
    void testAuthenticateFailure() throws Exception {
        when(mockDb.authenticate("wrong@example.com", 9999)).thenReturn(null);

        User result = loginManager.authenticate("wrong@example.com", 9999);
        assertNull(result);
        verify(mockDb).authenticate("wrong@example.com", 9999);
    }

    @Test
    void testRegisterStudent() throws Exception {
        Student student = new Student(0, "new@example.com", 1234, "NewStudent", "Science");
        doNothing().when(mockDb).saveStudent(student);

        loginManager.registerStudent(student);
        verify(mockDb).saveStudent(student);
    }
}