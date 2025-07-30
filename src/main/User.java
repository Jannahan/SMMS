package main;

// Abstract class representing a user in the system
public abstract class User {
    private int userId;       // Unique identifier for the user
    private String userEmail; // User's email address
    private int userPassword; // User's password

    // Constructor a new User with the given details
    public User(int id, String email, int password) {
        this.userId = id;
        this.userEmail = email;
        this.userPassword = password;
    }

    // Getter for user ID
    public int getUserId() {
        return userId;
    }

    // Setter for user ID
    public void setUserId(int id) {
        this.userId = id;
    }

    // Getter for user email
    public String getUserEmail() {
        return userEmail;
    }

    // Getter for user password
    public int getUserPassword() {
        return userPassword;
    }
}