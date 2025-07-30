package test;

import main.Database;
import main.LoginManager;
import main.Student;
import main.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LoginManagerTest {

    private LoginManager loginManager;
    private Database mockDb;

    @BeforeEach
    void setUp() {
        // Initialize the mock before each test
        mockDb = Mockito.mock(Database.class);
        loginManager = new LoginManager(mockDb);
    }

    @Test
    void testAuthenticateSuccess() throws Exception {
        // Mock a successful authentication
        User mockUser = Mockito.mock(User.class);
        when(mockDb.authenticate("test@example.com", 1234)).thenReturn(mockUser);

        User result = loginManager.authenticate("test@example.com", 1234);
        assertNotNull(result);
        assertEquals(mockUser, result);
        verify(mockDb).authenticate("test@example.com", 1234);
    }

    @Test
    void testAuthenticateFailure() throws Exception {
        // Mock a failed authentication
        when(mockDb.authenticate("wrong@example.com", 9999)).thenReturn(null);

        User result = loginManager.authenticate("wrong@example.com", 9999);
        assertNull(result);
        verify(mockDb).authenticate("wrong@example.com", 9999);
    }

    @Test
    void testRegisterStudent() throws Exception {
        // Mock the database save operation
        Student student = new Student(0, "new@example.com", 1234, "NewStudent", "Science");
        doNothing().when(mockDb).saveStudent(student);

        loginManager.registerStudent(student);
        verify(mockDb).saveStudent(student);
    }
}