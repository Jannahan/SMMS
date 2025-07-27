import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Class to manage student progress tracking
public class ProgressTracking {
    private Database database;

    // Constructor to initialize with a database instance
    public ProgressTracking(Database database) {
        this.database = database;
    }

    // Update a student's progress percentage
    public void updateProgress(Student student, int percentage) throws SQLException {
        String updateStudent = "UPDATE students SET progress_percentage = ? WHERE user_id = ?";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(updateStudent)) {
            stmt.setInt(1, percentage);
            stmt.setInt(2, student.getUserId());
            stmt.executeUpdate();
        }
        String insertLog = "INSERT INTO progress_updates (student_id, progress_percentage) VALUES (?, ?)";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(insertLog)) {
            stmt.setInt(1, student.getUserId());
            stmt.setInt(2, percentage);
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
        StringBuilder report = new StringBuilder("Student Report:\n");
        report.append("Name: ").append(student.getStudentName()).append("\n");
        report.append("Progress: ").append(viewProgress(student)).append("%\n");
        report.append("Interest: ").append(student.getInterest()).append("\n");
        report.append("Mentor: ").append(student.getAssignedMentor() != null ?
                student.getAssignedMentor().getMentorName() : "None").append("\n");
        return report.toString();
    }
}