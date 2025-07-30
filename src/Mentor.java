import java.util.*;

// Class representing a mentor, inheriting from User
public class Mentor extends User {
    private String mentorName;    // Name of the mentor
    private String expertise;     // Mentor's area of expertise
    private List<Student> assignedStudents; // List of students assigned to this mentor

    // Constructor to initialize a new mentor details
    public Mentor(int id, String email, int password, String mentorName, String expertise) {
        super(id, email, password);
        this.mentorName = mentorName;
        this.expertise = expertise;
        this.assignedStudents = new ArrayList<>();
    }

    // Getter for assigned students
    public List<Student> getAssignedStudents() {
        return assignedStudents;
    }

    // Add a student to the mentor's list
    public void addStudent(Student student) {
        assignedStudents.add(student);
    }

    // Getter for mentor name
    public String getMentorName() {
        return mentorName;
    }

    // Getter for mentor expertise
    public String getExpertise() {
        return expertise;
    }
}