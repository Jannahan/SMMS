package test;

import main.Database;
import main.Mentor;
import main.ProgressTracking;
import main.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProgressTrackingTest {

    @Mock
    private Database database;
    @Mock
    private Connection connection;
    @Mock
    private PreparedStatement stmt;
    @Mock
    private ResultSet rs;

    private ProgressTracking progressTracking;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        lenient().when(database.getConnection()).thenReturn(connection);
        lenient().when(connection.prepareStatement(anyString())).thenReturn(stmt);
        progressTracking = new ProgressTracking(database);
    }

    @Test
    void testUpdateProgress() throws SQLException {
        Student student = new Student(1, "student@example.com", 123, "Student Name", "Interest");
        int percentage = 50;
        progressTracking.updateProgress(student, percentage);
        verify(stmt).setInt(1, 50);
        verify(stmt).setInt(2, 1);
        verify(stmt).executeUpdate();
    }

    @Test
    void testViewProgress_RecordExists() throws SQLException {
        Student student = new Student(1, "student@example.com", 123, "Student Name", "Interest");
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("progress_percentage")).thenReturn(75);
        int progress = progressTracking.viewProgress(student);
        assertEquals(75, progress);
        verify(stmt).setInt(1, 1);
    }

    @Test
    void testViewProgress_NoRecord() throws SQLException {
        Student student = new Student(1, "student@example.com", 123, "Student Name", "Interest");
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        int progress = progressTracking.viewProgress(student);
        assertEquals(0, progress);
        verify(stmt).setInt(1, 1);
    }

    @Test
    void testGenerateDetailedReport_WithMentor() throws SQLException {
        Student student = new Student(1, "student@example.com", 123, "Student Name", "Interest");
        Mentor mentor = new Mentor(2, "mentor@example.com", 456, "Mentor Name", "Expertise");
        student.setAssignedMentor(mentor);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("progress_percentage")).thenReturn(80);
        String report = progressTracking.generateDetailedReport(student);
        assertEquals("Student Report:\n" +
                "Name: Student Name\n" +
                "Progress: 80%\n" +
                "Interest: Interest\n" +
                "Mentor: Mentor Name\n", report);
    }

    @Test
    void testGenerateDetailedReport_NoMentor() throws SQLException {
        Student student = new Student(1, "student@example.com", 123, "Student Name", "Interest");
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("progress_percentage")).thenReturn(60);
        String report = progressTracking.generateDetailedReport(student);
        assertEquals("Student Report:\n" +
                "Name: Student Name\n" +
                "Progress: 60%\n" +
                "Interest: Interest\n" +
                "Mentor: None\n", report);
    }

    @Test
    void testUpdateProgress_NullStudent() {
        assertThrows(NullPointerException.class, () -> progressTracking.updateProgress(null, 50));
    }

    @Test
    void testViewProgress_NullStudent() {
        assertThrows(NullPointerException.class, () -> progressTracking.viewProgress(null));
    }

    @Test
    void testGenerateDetailedReport_NullStudent() {
        assertThrows(NullPointerException.class, () -> progressTracking.generateDetailedReport(null));
    }
}