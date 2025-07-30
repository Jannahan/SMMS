package test;

import main.Database;
import main.MatchingService;
import main.Mentor;
import main.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MatchingServiceTest {

    @Mock
    private Database database;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet rs;

    private MatchingService matchingService;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        Mockito.lenient().when(database.getConnection()).thenReturn(connection);
        Mockito.lenient().when(connection.prepareStatement(anyString())).thenReturn(stmt);
        matchingService = new MatchingService(database);
    }

    @Test
    void testMatchStudentToMentor_MentorsAvailable() throws SQLException {
        // Arrange
        Student student = new Student(1, "student@example.com", 123, "Student", "Interest");
        Mentor mentor1 = new Mentor(2, "mentor1@example.com", 456, "Mentor1", "Expertise");
        Mentor mentor2 = new Mentor(3, "mentor2@example.com", 789, "Mentor2", "Expertise");
        List<Mentor> mentors = new ArrayList<>();
        mentors.add(mentor1);
        mentors.add(mentor2);

        when(database.getAllMentors()).thenReturn(mentors);
        when(database.getMentor(mentor1.getUserId(), true)).thenReturn(mentor1);

        // Act
        Mentor assignedMentor = matchingService.matchStudentToMentor(student);

        // Assert
        assertNotNull(assignedMentor);
        assertEquals(mentor1, assignedMentor);
        verify(stmt).setInt(1, mentor1.getUserId());
        verify(stmt).setInt(2, student.getUserId());
        verify(stmt).executeUpdate();
    }

    @Test
    void testMatchStudentToMentor_NoMentorsAvailable() throws SQLException {
        // Arrange
        Student student = new Student(1, "student@example.com", 123, "Student", "Interest");
        when(database.getAllMentors()).thenReturn(new ArrayList<>());

        // Act
        Mentor assignedMentor = matchingService.matchStudentToMentor(student);

        // Assert
        assertNull(assignedMentor);
    }

    @Test
    void testAssignUnassignedStudents_StudentsAvailable() throws SQLException {
        // Arrange
        Mentor mentor = new Mentor(1, "mentor@example.com", 456, "Mentor", "Expertise");
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt("user_id")).thenReturn(2);
        Student student = new Student(2, "student@example.com", 123, "Student", "Interest");
        when(database.getStudent(2, true)).thenReturn(student);

        // Act
        matchingService.assignUnassignedStudents(mentor);

        // Assert
        verify(stmt).setInt(1, mentor.getUserId());
        verify(stmt).setInt(2, student.getUserId());
        verify(stmt).executeUpdate();
    }

    @Test
    void testAssignUnassignedStudents_NoStudentsAvailable() throws SQLException {
        // Arrange
        Mentor mentor = new Mentor(1, "mentor@example.com", 456, "Mentor", "Expertise");
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        // Act
        matchingService.assignUnassignedStudents(mentor);

        // Assert
        verify(stmt, never()).setInt(anyInt(), anyInt());
        verify(stmt, never()).executeUpdate();
    }
}