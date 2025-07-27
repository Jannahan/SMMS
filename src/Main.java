import java.sql.*;
import java.util.*;

// Main class to run the student-mentor system
public class Main {
    public static void main(String[] args) {
        try {
            Database db = new Database();
            LoginManager loginManager = new LoginManager(db);
            MatchingService matchingService = new MatchingService(db);
            CommunicationManager commManager = new CommunicationManager(db);
            ProgressTracking progressTracking = new ProgressTracking(db);
            Scanner scanner = new Scanner(System.in);
            User currentUser = null;

            while (true) {
                if (currentUser == null) {
                    displayMainMenu();
                    try {
                        int choice = scanner.nextInt();
                        scanner.nextLine();
                        switch (choice) {
                            case 1: // Register
                                handleRegistration(scanner, loginManager, matchingService, db);
                                break;
                            case 2: // Login
                                currentUser = handleLogin(scanner, loginManager, matchingService);
                                break;
                            case 3: // Exit
                                db.close();
                                System.out.println("Goodbye!");
                                return;
                            default:
                                System.out.println("Invalid choice! Please enter a number between 1 and 3.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input! Please enter a number between 1 and 3.");
                        scanner.nextLine();
                    }
                } else if (currentUser instanceof Student student) {
                    handleStudentMenu(student, scanner, progressTracking, commManager, loginManager, db);
                    if (!scanner.hasNext()) currentUser = null;
                } else if (currentUser instanceof Mentor mentor) {
                    handleMentorMenu(mentor, scanner, commManager, loginManager, db);
                    if (!scanner.hasNext()) currentUser = null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Display the main menu
    private static void displayMainMenu() {
        System.out.println("\n=== Student Mentor Management System ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice here: ");
    }

    // Get valid integer choice with error handling
    private static int getValidChoice(Scanner scanner) {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // Handle user registration
    private static void handleRegistration(Scanner scanner, LoginManager loginManager, MatchingService matchingService, Database db) throws SQLException {
        while (true) {
            System.out.println("\n=== Registration ===");
            System.out.println("1. Register as Student");
            System.out.println("2. Register as Mentor");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice here: ");
            int type = getValidChoice(scanner);
            scanner.nextLine();
            if (type == 3) break; // Return to main menu
            if (type != 1 && type != 2) {
                System.out.println("Invalid user selection! please try again.");
                continue;
            }
            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email! Must contain '@' and '.com'.");
                continue;
            }
            if (db.authenticate(email, 0) != null) {
                System.out.println("Email already registered!");
                continue;
            }
            System.out.print("Password: ");
            int password = getValidChoice(scanner);
            scanner.nextLine();
            System.out.print("Name: ");
            String name = scanner.nextLine();
            if (!isValidName(name)) {
                System.out.println("Invalid name! Must not contain any numbers.");
                continue;
            }
            if (type == 1) {
                System.out.print("Interest: ");
                String interest = scanner.nextLine();
                if (!isValidName(interest)) {
                    System.out.println("Invalid interest! Must not any contain numbers.");
                    continue;
                }
                Student student = new Student(0, email, password, name, interest);
                loginManager.registerStudent(student);
                System.out.println("Student registered successfully!");
                Mentor mentor = matchingService.matchStudentToMentor(student);
                System.out.println(mentor != null ? "Assigned to mentor: " + mentor.getMentorName() : "No mentor assigned.");
                break;
            } else if (type == 2) {
                System.out.print("Expertise: ");
                String expertise = scanner.nextLine();
                if (!isValidName(expertise)) {
                    System.out.println("Invalid expertise! Must not contain any numbers.");
                    continue;
                }
                Mentor mentor = new Mentor(0, email, password, name, expertise);
                loginManager.registerMentor(mentor);
                System.out.println("Mentor registered successfully!");
                break;
            }
        }
    }

    // Handle user login
    private static User handleLogin(Scanner scanner, LoginManager loginManager, MatchingService matchingService) throws SQLException {
        while (true) {
            System.out.println("\n=== Login ===");
            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email! Must contain '@' and '.com'.");
                continue;
            }
            System.out.print("Password: ");
            int password = getValidChoice(scanner);
            scanner.nextLine();
            User user = loginManager.authenticate(email, password);
            if (user != null) {
                System.out.println("Login successful!");
                if (user instanceof Student student && student.getAssignedMentor() == null) {
                    Mentor mentor = matchingService.matchStudentToMentor(student);
                    System.out.println(mentor != null ? "Assigned to mentor: " + mentor.getMentorName() : "No mentor assigned.");
                }
                return user;
            } else {
                System.out.println("Invalid credentials! Enter 0 to go back to main menu, or try again.");
                int back = getValidChoice(scanner);
                if (back == 0) return null;
            }
        }
    }

    // Validate email contains '@' and '.com'
    private static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".com");
    }

    // Validate name/interest does not contain numbers
    private static boolean isValidName(String text) {
        return text != null && !text.matches(".*\\d.*");
    }

    // Handle student menu options
    private static void handleStudentMenu(Student student, Scanner scanner, ProgressTracking progressTracking,
                                          CommunicationManager commManager, LoginManager loginManager, Database db) throws SQLException {
        while (true) {
            System.out.println("\n=== Student Menu ===");
            System.out.println("1. View Progress");
            System.out.println("2. Update Progress");
            System.out.println("3. Generate Report");
            System.out.println("4. Send Message");
            System.out.println("5. Send Emergency Notification");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 6) {
                    System.out.println("Logged out!");
                    return; // Returns to main menu by exiting the method
                }
                switch (choice) {
                    case 1:
                        System.out.println("Progress: " + progressTracking.viewProgress(student) + "%");
                        break;
                    case 2:
                        System.out.print("New progress (0-100): ");
                        int progress = scanner.nextInt();
                        scanner.nextLine();
                        if (progress >= 0 && progress <= 100) {
                            progressTracking.updateProgress(student, progress);
                            System.out.println("Progress updated!");
                        } else {
                            System.out.println("Invalid progress value!");
                        }
                        break;
                    case 3:
                        System.out.println(progressTracking.generateDetailedReport(student));
                        break;
                    case 4:
                        sendMessage(scanner, student, commManager, loginManager, db, student.getAssignedMentor());
                        break;
                    case 5:
                        sendEmergencyNotification(scanner, commManager, loginManager, db);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // Handle mentor menu options
    private static void handleMentorMenu(Mentor mentor, Scanner scanner, CommunicationManager commManager,
                                         LoginManager loginManager, Database db) throws SQLException {
        while (true) {
            System.out.println("\n=== Mentor Menu ===");
            System.out.println("1. View Assigned Students");
            System.out.println("2. Send Message");
            System.out.println("3. Send Emergency Notification");
            System.out.println("4. Logout");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice == 4) {
                    System.out.println("Logged out!");
                    return; // Returns to main menu by exiting the method
                }
                switch (choice) {
                    case 1:
                        List<Student> students = mentor.getAssignedStudents();
                        if (students.isEmpty()) {
                            System.out.println("No students assigned.");
                        } else {
                            System.out.println("Assigned Students:");
                            for (Student s : students) {
                                System.out.println("- " + s.getStudentName());
                            }
                        }
                        break;
                    case 2:
                        sendMessageToStudent(scanner, mentor, commManager, loginManager);
                        break;
                    case 3:
                        sendEmergencyNotification(scanner, commManager, loginManager, db);
                        break;
                    default:
                        System.out.println("Invalid choice!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // Send a message to a specific user
    private static void sendMessage(Scanner scanner, User from, CommunicationManager commManager,
                                    LoginManager loginManager, Database db, User expectedRecipient) throws SQLException {
        System.out.print("Recipient email: ");
        String email = scanner.nextLine();
        User recipient = db.getUserByEmail(email); // Use database method to find user by email
        if (recipient != null) {
            if (from instanceof Student && recipient instanceof Mentor) {
                Student student = (Student) from;
                Mentor mentor = (Mentor) recipient;
                if (student.getAssignedMentor() != null && student.getAssignedMentor().getUserId() == mentor.getUserId()) {
                    System.out.print("Message: ");
                    String message = scanner.nextLine();
                    commManager.sendMessage(from, recipient, message);
                    System.out.println("Message sent successfully!");
                } else {
                    System.out.println("You can only message your assigned mentor!");
                }
            } else {
                System.out.println("Invalid recipient type!");
            }
        } else {
            System.out.println("Recipient not found!");
        }
    }

    // Send a message to an assigned student
    private static void sendMessageToStudent(Scanner scanner, Mentor mentor, CommunicationManager commManager,
                                             LoginManager loginManager) throws SQLException {
        System.out.print("Recipient email: ");
        String email = scanner.nextLine();
        User recipient = loginManager.authenticate(email, 0);
        if (recipient instanceof Student student && mentor.getAssignedStudents().contains(student)) {
            System.out.print("Message: ");
            String message = scanner.nextLine();
            commManager.sendMessage(mentor, student, message);
        } else {
            System.out.println("Recipient not found or not an assigned student!");
        }
    }

    // Send an emergency notification
    private static void sendEmergencyNotification(Scanner scanner, CommunicationManager commManager,
                                                  LoginManager loginManager, Database db) throws SQLException {
        System.out.print("Recipient email: ");
        String email = scanner.nextLine();
        User recipient = db.getUserByEmail(email);
        if (recipient != null) {
            System.out.print("Emergency message: ");
            String message = scanner.nextLine();
            commManager.sendEmergencyNotification(recipient, message);
        } else {
            System.out.println("Recipient not found!");
        }
    }
}