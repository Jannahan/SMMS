package test;

import main.Student;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StudentTest {

    @Test
    void testStudentConstructor() {
        // Test the constructor with valid inputs
        Student student = new Student(1, "test@example.com", 1234, "TestName", "Math");
        assertEquals(1, student.getUserId());
        assertEquals("test@example.com", student.getUserEmail());
        assertEquals(1234, student.getUserPassword());
        assertEquals("TestName", student.getStudentName());
        assertEquals("Math", student.getInterest());
        assertEquals(0, student.getProgress()); // Assuming default progress is 0
    }

    @Test
    void testSetProgressValid() {
        // Test setting a valid progress value
        Student student = new Student(1, "test@example.com", 1234, "TestName", "Math");
        student.setProgress(50);
        assertEquals(50, student.getProgress());
    }

    @Test
    void testSetProgressInvalid() {
        // Test invalid progress values (e.g., > 100 or < 0)
        Student student = new Student(1, "test@example.com", 1234, "TestName", "Math");
        assertThrows(IllegalArgumentException.class, () -> student.setProgress(101));
        assertThrows(IllegalArgumentException.class, () -> student.setProgress(-1));
    }
}