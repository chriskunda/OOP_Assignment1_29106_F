package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public String getSemester() { return semester; }
    public String getEnrollmentStatus() { return enrollmentStatus; }

    @Override
    public String getId() { return enrollmentId; }
    @Override
    public String getDisplayName() { return student.getDisplayName() + " -> " + course.getDisplayName(); }
    @Override
    public boolean isActive() { return "ACTIVE".equalsIgnoreCase(enrollmentStatus); }

    @Override
    public void validate() throws ValidationException {
        if (enrollmentId == null || enrollmentId.isBlank()) throw new ValidationException("Enrollment ID cannot be blank.");
        if (student == null) throw new ValidationException("Student must be specified.");
        if (course == null) throw new ValidationException("Course must be specified.");
    }

    @Override
    public void printDetails() { System.out.println(getRegistrationSummary()); }

    @Override
    public String getRegistrationSummary() {
        return String.format("Enrollment [%s] | Student: %s | Course: %s | Semester: %s", enrollmentId, student.getDisplayName(), course.getDisplayName(), semester);
    }
}