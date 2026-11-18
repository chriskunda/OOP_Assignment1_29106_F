package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher; 

    public Department(String departmentId, String departmentName, String departmentCode, String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public Teacher getHeadTeacher() { return headTeacher; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }

    @Override
    public String getId() { return departmentId; }

    @Override
    public String getDisplayName() { return departmentName; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        if (departmentId == null || departmentId.isBlank()) throw new ValidationException("Department ID cannot be blank.");
        if (departmentName == null || departmentName.length() < MIN_NAME_LENGTH || departmentName.length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Department name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        if (contactEmail == null || !contactEmail.contains("@")) throw new ValidationException("Invalid contact email.");
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return String.format("[%s] %s (%s) - Office: %s", departmentId, departmentName, departmentCode, officeLocation);
    }

    @Override
    public String toString() {
        return "[" + departmentId + "] " + departmentName;
    }
}