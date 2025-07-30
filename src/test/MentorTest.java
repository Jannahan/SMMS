package test;

import main.Mentor;
import main.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MentorTest {
    private Mentor mentor;

    @BeforeEach
    void setUp() {
        mentor = new Mentor(1, "mentor@example.com", 123, "Mentor Name", "Expertise");
    }

    @Test
    void testConstructorAndGetters() {
        assertEquals("Mentor Name", mentor.getMentorName());
        assertEquals("Expertise", mentor.getExpertise());
        assertTrue(mentor.getAssignedStudents().isEmpty());
    }

    @Test
    void testAddStudent() {
        Student student1 = new Student(2, "student1@example.com", 456, "Student One", "Interest");
        mentor.addStudent(student1);
        List<Student> students = mentor.getAssignedStudents();
        assertEquals(1, students.size());
        assertSame(student1, students.get(0));

        Student student2 = new Student(3, "student2@example.com", 789, "Student Two", "Interest");
        mentor.addStudent(student2);
        assertEquals(2, students.size());
        assertSame(student2, students.get(1));
    }

    @Test
    void testAddStudent_DuplicatesAllowed() {
        Student student = new Student(2, "student@example.com", 456, "Student", "Interest");
        mentor.addStudent(student);
        mentor.addStudent(student);
        List<Student> students = mentor.getAssignedStudents();
        assertEquals(2, students.size());
        assertSame(student, students.get(0));
        assertSame(student, students.get(1));
    }

    @Test
    void testGetAssignedStudents_ReturnsModifiableList() {
        Student student = new Student(2, "student@example.com", 456, "Student", "Interest");
        mentor.addStudent(student);
        List<Student> students = mentor.getAssignedStudents();
        students.clear();
        assertTrue(mentor.getAssignedStudents().isEmpty());
    }

    @Test
    void testConstructor_SetsUserFields() {
        assertEquals(1, mentor.getUserId());
        assertEquals("mentor@example.com", mentor.getUserEmail());
        assertEquals(123, mentor.getUserPassword());
    }

    @Test
    void testSetUserId() {
        mentor.setUserId(10);
        assertEquals(10, mentor.getUserId());
    }

    @Test
    void testAddStudent_NullStudent() {
        mentor.addStudent(null);
        List<Student> students = mentor.getAssignedStudents();
        assertEquals(1, students.size());
        assertNull(students.get(0));
    }
}