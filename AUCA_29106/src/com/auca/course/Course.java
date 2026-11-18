package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCreditHours() { return creditHours; }
    public Department getDepartment() { return department; }
    public Teacher getAssignedTeacher() { return assignedTeacher; }

    @Override
    public String getId() { return courseId; }
    @Override
    public String getDisplayName() { return courseTitle; }
    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        if (courseId == null || courseId.isBlank()) throw new ValidationException("Course ID cannot be blank.");
        if (creditHours < 1 || creditHours > 6) throw new ValidationException("Credit hours must be between 1 and 6.");
        if (department == null) throw new ValidationException("Course must reference a department.");
        if (assignedTeacher == null) throw new ValidationException("Course must reference a teacher.");
    }

    @Override
    public void printDetails() { System.out.println(getRegistrationSummary()); }

    @Override
    public String getRegistrationSummary() {
        return String.format("[%s] %s (%s) - Credits: %d", courseId, courseTitle, courseCode, creditHours);
    }

    @Override
    public String toString() { return "[" + courseId + "] " + courseTitle; }
}