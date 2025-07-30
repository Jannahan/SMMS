package test;

import main.Database;
import main.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.*;

class DatabaseTest {

    @Test
    void testSaveStudent() throws SQLException {
        Database db = new Database();
        Student student = new Student(0, "save@example.com", 5678, "SaveStudent", "History");
        db.saveStudent(student);

        // Retrieve and verify the saved student
        Student retrieved = db.getStudent(student.getUserId(), false);
        assertNotNull(retrieved);
        assertEquals("save@example.com", retrieved.getUserEmail());
        assertEquals("SaveStudent", retrieved.getStudentName());
    }

    @Test
    void testGetStudentNotFound() throws SQLException {
        Database db = new Database();
        Student student = db.getStudent(9999, false); // Non-existent ID
        assertNull(student);
    }
}