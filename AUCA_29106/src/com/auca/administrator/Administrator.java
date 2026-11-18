package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public class Administrator extends Person {
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth,
                         String staffNumber, String jobTitle, String assignedOffice, LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public String getRoleDescription() { return "Administrator - " + jobTitle; }
    @Override
    public boolean isActive() { return active; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (staffNumber == null || staffNumber.isBlank()) throw new ValidationException("Staff number cannot be blank.");
    }

    @Override
    public String getRegistrationSummary() {
        return String.format("[%s] Administrator %s %s | Title: %s", personId, firstName, lastName, jobTitle);
    }
}