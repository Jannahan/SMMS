package main;

// Class representing a student, inheriting from User
public class Student extends User {
    private String studentName;   // Name of the student
    private String interest;      // Student's area of interest
    private Mentor assignedMentor; // Reference to the assigned mentor
    private int progress; //Progress percentage (0-100)

    // Constructor to initialize student details
    public Student(int id, String email, int password, String studentName, String interest) {
        super(id, email, password);
        this.studentName = studentName;
        this.interest = interest;
        this.progress = 0; //default progress
    }

    // Getter for assigned mentor
    public Mentor getAssignedMentor() {
        return assignedMentor;
    }

    // Setter for assigned mentor
    public void setAssignedMentor(Mentor mentor) {
        this.assignedMentor = mentor;
    }

    // Getter for student name
    public String getStudentName() {
        return studentName;
    }

    // Getter for student interest
    public String getInterest() {
        return interest;
    }

    // Getter for progress
    public int getProgress() {
        return progress;
    }

    // Setter for progress
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= 100) {
            this.progress = progress;
        } else {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
    }
}
