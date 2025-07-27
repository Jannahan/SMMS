import java.sql.*;
import java.util.*;

// Class to match students with mentors
public class MatchingService {
    private Database database;

    // Constructor to initialize with a database instance
    public MatchingService(Database database) {
        this.database = database;
    }

    // Match a student to a mentor (simple implementation: picks first available mentor)
    public Mentor matchStudentToMentor(Student student) throws SQLException {
        List<Mentor> mentors = database.getAllMentors();
        if (mentors.isEmpty()) {
            System.out.println("No mentors available for matching.");
            return null;
        }
        Mentor mentor = mentors.getFirst(); // Simple matching: first mentor
        student.setAssignedMentor(mentor);
        mentor.addStudent(student);
        String sql = "UPDATE students SET mentor_id = ? WHERE user_id = ?";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, mentor.getUserId());
            stmt.setInt(2, student.getUserId());
            stmt.executeUpdate();
        }
        // Reload mentor to ensure assignedStudents is up-to-date (optional but recommended)
        return database.getMentor(mentor.getUserId(), true); // Load related students
    }
}