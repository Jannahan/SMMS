import java.sql.*;

// Class to handle user authentication and registration
public class LoginManager {
    private Database database;

    // Constructor to initialize with a database instance
    public LoginManager(Database database) {
        this.database = database;
    }

    // Authenticate a user with email and password
    public User authenticate(String email, int password) throws SQLException {
        return database.authenticate(email, password);
    }

    // Register a new student
    public void registerStudent(Student student) throws SQLException {
        database.saveStudent(student);
    }

    // Register a new mentor
    public void registerMentor(Mentor mentor) throws SQLException {
        database.saveMentor(mentor);
    }
}