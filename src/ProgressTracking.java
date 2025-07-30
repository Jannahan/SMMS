import java.sql.*;

// Class to manage student progress tracking
public class ProgressTracking {
    private Database database;

    // Constructor to initialize a new Progress Tracking with a database instance
    public ProgressTracking(Database database) {
        this.database = database;
    }

    // Update a student's progress percentage in the database
    public void updateProgress(Student student, int percentage) throws SQLException {
        String updateStudent = "UPDATE students SET progress_percentage = ? WHERE user_id = ?";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(updateStudent)) {
            stmt.setInt(1, percentage);
            stmt.setInt(2, student.getUserId());
            stmt.executeUpdate();
        }
    }

    // Retrieve a student's current progress percentage
    public int viewProgress(Student student) throws SQLException {
        String query = "SELECT progress_percentage FROM students WHERE user_id = ?";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(query)) {
            stmt.setInt(1, student.getUserId());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("progress_percentage");
                }
            }
        }
        return 0;
    }

    // Generate a detailed report for a student
    public String generateDetailedReport(Student student) throws SQLException {
        return "Student Report:\n" + "Name: " + student.getStudentName() + "\n" +
                "Progress: " + viewProgress(student) + "%\n" +
                "Interest: " + student.getInterest() + "\n" +
                "Mentor: " + (student.getAssignedMentor() != null ?
                student.getAssignedMentor().getMentorName() : "None") +
                "\n";
    }
}