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

        // Find mentor with the fewest students or use first if no preference
        Mentor selectedMentor = mentors.get(0);
        int minStudents = selectedMentor.getAssignedStudents().size();
        for (Mentor mentor : mentors) {
            int studentCount = mentor.getAssignedStudents().size();
            if (studentCount < minStudents) {
                minStudents = studentCount;
                selectedMentor = mentor;
            }
        }
        student.setAssignedMentor(selectedMentor);
        selectedMentor.addStudent(student);
        String sql = "UPDATE students SET mentor_id = ? WHERE user_id = ?";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, selectedMentor.getUserId());
            stmt.setInt(2, student.getUserId());
            stmt.executeUpdate();
        }
        // Reload mentor to ensure consistency
        return database.getMentor(selectedMentor.getUserId(), true);
    }

    // Assign unassigned students to a mentor
    public void assignUnassignedStudents(Mentor mentor) throws SQLException {
        String sql = "SELECT user_id FROM students WHERE mentor_id IS NULL";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int studentId = rs.getInt("user_id");
                Student student = database.getStudent(studentId, true);
                if (student != null && student.getAssignedMentor() == null) {
                    student.setAssignedMentor(mentor);
                    mentor.addStudent(student);
                    String updateSql = "UPDATE students SET mentor_id = ? WHERE user_id = ?";
                    try (PreparedStatement updateStmt = database.getConnection().prepareStatement(updateSql)) {
                        updateStmt.setInt(1, mentor.getUserId());
                        updateStmt.setInt(2, studentId);
                        updateStmt.executeUpdate();
                    }
                }
            }
            // Reload mentor to reflect new assignments
            database.getMentor(mentor.getUserId(), true);
        }
    }
}
