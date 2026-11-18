package com.auca.abstracts;

import com.auca.department.Department; // Note: Will create department package next
import com.auca.exceptions.ValidationException;

import java.time.LocalDate;

public abstract class AcademicMember extends Person {
    // 6 Mandatory Attributes for AcademicMember
    protected String memberNumber;
    protected Department department;
    protected LocalDate joinDate;
    protected String academicStatus;
    protected String institutionalEmail;
    protected String advisorName;

    // Parameterized Constructor (using super to initialize Person fields)
    public AcademicMember(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth,
                          String memberNumber, Department department, LocalDate joinDate, String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    // Abstract method required by assignment
    public abstract String getAcademicCategory();

    // Getters and Setters
    public String getMemberNumber() { return memberNumber; }
    public void setMemberNumber(String memberNumber) { this.memberNumber = memberNumber; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public LocalDate getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }

    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }

    public String getInstitutionalEmail() { return institutionalEmail; }
    public void setInstitutionalEmail(String institutionalEmail) { this.institutionalEmail = institutionalEmail; }

    public String getAdvisorName() { return advisorName; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(academicStatus);
    }

    @Override
    public void validate() throws ValidationException {
        super.validate(); // Validate parent Person fields first
        if (memberNumber == null || memberNumber.trim().isEmpty()) {
            throw new ValidationException("Member number cannot be blank.");
        }
        if (department == null) {
            throw new ValidationException("Department must be assigned.");
        }
        if (joinDate == null || joinDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Join date cannot be in the future.");
        }
        if (institutionalEmail == null || !institutionalEmail.contains("@")) {
            throw new ValidationException("Institutional email is invalid.");
        }
    }

    @Override
    public String toString() {
        return super.toString() + ", AcademicMember{" +
                "memberNumber='" + memberNumber + '\'' +
                ", academicStatus='" + academicStatus + '\'' +
                '}';
    }
}