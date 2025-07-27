import java.sql.*;
import java.util.*;

// Class to manage database operations for the student-mentor system
public class Database {
    private Connection connection; // Database connection instance

    // Constructor to establish database connection and create tables
    public Database() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/student_mentor_db";
        String user = "root";
        String password = "Swix@7466"; // Replace with your actual password
        try {
            connection = DriverManager.getConnection(url, user, password);
            createTables();
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database: " + e.getMessage());
        }
    }

    // Create database tables if they do not exist
    private void createTables() throws SQLException {
        String[] queries = {
                "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "email VARCHAR(255) UNIQUE, " +
                        "password INT, " +
                        "user_type VARCHAR(10))",
                "CREATE TABLE IF NOT EXISTS mentors (" +
                        "user_id INT PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "expertise VARCHAR(255), " +
                        "FOREIGN KEY (user_id) REFERENCES users(id))",
                "CREATE TABLE IF NOT EXISTS students (" +
                        "user_id INT PRIMARY KEY, " +
                        "name VARCHAR(255), " +
                        "interest VARCHAR(255), " +
                        "progress_percentage INT DEFAULT 0, " +
                        "mentor_id INT, " +
                        "FOREIGN KEY (user_id) REFERENCES users(id), " +
                        "FOREIGN KEY (mentor_id) REFERENCES mentors(user_id))",
                "CREATE TABLE IF NOT EXISTS communication_log (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "from_id INT, " +
                        "to_id INT, " +
                        "message TEXT, " +
                        "type VARCHAR(20), " +
                        "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (from_id) REFERENCES users(id), " +
                        "FOREIGN KEY (to_id) REFERENCES users(id))",
                "CREATE TABLE IF NOT EXISTS progress_updates (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "student_id INT, " +
                        "progress_percentage INT, " +
                        "update_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (student_id) REFERENCES users(id))"
        };
        try (Statement stmt = connection.createStatement()) {
            for (String query : queries) {
                stmt.execute(query);
            }
        }
    }

    // Get the database connection
    public Connection getConnection() {
        return connection;
    }

    // Save a student to the database
    public void saveStudent(Student s) throws SQLException {
        String insertUser = "INSERT INTO users (email, password, user_type) VALUES (?, ?, 'student')";
        try (PreparedStatement stmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, s.getUserEmail());
            stmt.setInt(2, s.getUserPassword());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    s.setUserId(userId);
                    String insertStudent = "INSERT INTO students (user_id, name, interest) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt2 = connection.prepareStatement(insertStudent)) {
                        stmt2.setInt(1, userId);
                        stmt2.setString(2, s.getStudentName());
                        stmt2.setString(3, s.getInterest());
                        stmt2.executeUpdate();
                    }
                }
            }
        }
    }

    // Save a mentor to the database
    public void saveMentor(Mentor m) throws SQLException {
        String insertUser = "INSERT INTO users (email, password, user_type) VALUES (?, ?, 'mentor')";
        try (PreparedStatement stmt = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, m.getUserEmail());
            stmt.setInt(2, m.getUserPassword());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    m.setUserId(userId);
                    String insertMentor = "INSERT INTO mentors (user_id, name, expertise) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt2 = connection.prepareStatement(insertMentor)) {
                        stmt2.setInt(1, userId);
                        stmt2.setString(2, m.getMentorName());
                        stmt2.setString(3, m.getExpertise());
                        stmt2.executeUpdate();
                    }
                }
            }
        }
    }

    // Retrieve a student by ID
    public Student getStudent(int id, boolean loadRelated) throws SQLException {
        String query = "SELECT u.id, u.email, u.password, s.name, s.interest, s.mentor_id " +
                "FROM users u JOIN students s ON u.id = s.user_id WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(rs.getInt("id"), rs.getString("email"), rs.getInt("password"),
                            rs.getString("name"), rs.getString("interest"));
                    int mentorId = rs.getInt("mentor_id");
                    if (loadRelated && !rs.wasNull()) {
                        student.setAssignedMentor(getMentor(mentorId, false)); // Avoid recursive loading
                    }
                    return student;
                }
            }
        }
        return null;
    }

    // Retrieve a mentor by ID
// Retrieve a mentor by ID with optional related object loading
    public Mentor getMentor(int id, boolean loadRelated) throws SQLException {
        String query = "SELECT u.id, u.email, u.password, m.name, m.expertise " +
                "FROM users u JOIN mentors m ON u.id = m.user_id WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mentor mentor = new Mentor(rs.getInt("id"), rs.getString("email"), rs.getInt("password"),
                            rs.getString("name"), rs.getString("expertise"));
                    if (loadRelated) {
                        String studentQuery = "SELECT user_id FROM students WHERE mentor_id = ?";
                        try (PreparedStatement stmt2 = connection.prepareStatement(studentQuery)) {
                            stmt2.setInt(1, id);
                            try (ResultSet rs2 = stmt2.executeQuery()) {
                                while (rs2.next()) {
                                    Student student = getStudent(rs2.getInt("user_id"), false); // Avoid recursive loading
                                    if (student != null) {
                                        mentor.addStudent(student);
                                    }
                                }
                            }
                        }
                    }
                    return mentor;
                }
            }
        }
        return null;
    }

    // Authenticate a user by email and password
        public User authenticate(String email, int password) throws SQLException {
            String query = "SELECT id, user_type FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, email);
                stmt.setInt(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("user_type");
                        return "student".equals(type) ? getStudent(id, true) : getMentor(id, true); // Initial load without related objects
                    }
                }
            }
            return null;
        }

    // Retrieve all mentors from the database
        public List<Mentor> getAllMentors() throws SQLException {
            List<Mentor> mentors = new ArrayList<>();
            String query = "SELECT user_id FROM mentors";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Mentor mentor = getMentor(rs.getInt("user_id"), false); // Initial load without related objects
                    if (mentor != null) {
                        mentors.add(mentor);
                    }
                }
            }
            return mentors;
    }

    public User getUserByEmail(String email) throws SQLException {
        String query = "SELECT id, user_type FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String type = rs.getString("user_type");
                    return "student".equals(type) ? getStudent(id, false) : getMentor(id, false);
                }
            }
        }
        return null;
    }

    // Close the database connection
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}