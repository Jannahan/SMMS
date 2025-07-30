package test;

import main.CommunicationManager;
import main.Database;
import main.Mentor;
import main.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

public class CommunicationManagerTest {
    private CommunicationManager commManager;
    private Database database;
    private Connection connection;
    private PreparedStatement stmt;

    @BeforeEach
    void setUp() throws SQLException {
        database = Mockito.mock(Database.class);
        connection = Mockito.mock(Connection.class);
        stmt = Mockito.mock(PreparedStatement.class);

        Mockito.lenient().when(database.getConnection()).thenReturn(connection);
        Mockito.lenient().when(connection.prepareStatement(anyString())).thenReturn(stmt);

        commManager = new CommunicationManager(database);
    }

    @Test
    void testSendMessageNullSender() {
        User to = new Mentor(2, "to@example.com", 456, "To", "Expertise");
        assertThrows(IllegalArgumentException.class, () -> commManager.sendMessage(null, to, "Hello"));
    }
}