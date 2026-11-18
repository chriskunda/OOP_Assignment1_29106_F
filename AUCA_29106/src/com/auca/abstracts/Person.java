package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {
    
    protected String personId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected LocalDate dateOfBirth;

    
    public Person(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    
    public abstract String getRoleDescription();

    // Getters and Setters
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    
    @Override
    public String getId() {
        return personId;
    }

    @Override
    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean isActive() {
        return true; // Default active state for persons
    }

    
    @Override
    public void validate() throws ValidationException {
        if (personId == null || personId.trim().isEmpty()) {
            throw new ValidationException("Person ID cannot be blank.");
        }
        if (firstName == null || firstName.trim().length() < MIN_NAME_LENGTH || firstName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("First name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        if (lastName == null || lastName.trim().length() < MIN_NAME_LENGTH || lastName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Last name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        if (email == null || !email.contains("@") || email.length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException("Invalid email format or length.");
        }
        if (phoneNumber == null || phoneNumber.length() < MIN_PHONE_LENGTH || phoneNumber.length() > MAX_PHONE_LENGTH) {
            throw new ValidationException("Phone number must be between " + MIN_PHONE_LENGTH + " and " + MAX_PHONE_LENGTH + " digits.");
        }
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return "Person ID: " + personId + ", Name: " + firstName + " " + lastName + ", Email: " + email;
    }

    @Override
    public String toString() {
        return "Person{" +
                "personId='" + personId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}