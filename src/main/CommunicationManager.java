package main;

import java.sql.*;

// Class to manage communication between users
public class CommunicationManager {
    private Database database;

    // Constructor to initialize with a database instance
    public CommunicationManager(Database database) {
        this.database = database;
    }

    // Send a message from one user to another
    public void sendMessage(User from, User to, String message) throws SQLException {
        System.out.println("Message sent from " + from.getUserEmail() + " to " + to.getUserEmail() + ": " + message);
        logCommunication(from, to, message, "message");
    }

    // Send an emergency notification to a user
    public void sendEmergencyNotification(User to, String message) throws SQLException {
        System.out.println("Emergency notification to " + to.getUserEmail() + ": " + message);
        logCommunication(null, to, message, "emergency");
    }

    // Log communication details to the database
    private void logCommunication(User from, User to, String message, String type) throws SQLException {
        String sql = "INSERT INTO communication_log (from_id, to_id, message, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = database.getConnection().prepareStatement(sql)) {
            if (from != null) {
                stmt.setInt(1, from.getUserId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, to.getUserId());
            stmt.setString(3, message);
            stmt.setString(4, type);
            stmt.executeUpdate();
        }
    }
}