package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType, double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() { return assessmentId; }
    @Override
    public String getDisplayName() { return assessmentType + " (" + obtainedScore + "/" + maximumScore + ")"; }
    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null || assessmentId.isBlank()) throw new ValidationException("Assessment ID cannot be blank.");
        if (enrollment == null) throw new ValidationException("Enrollment must be specified.");
        if (maximumScore <= 0) throw new ValidationException("Maximum score must be greater than zero.");
        if (obtainedScore < 0 || obtainedScore > maximumScore) throw new ValidationException("Obtained score must be between 0 and maximum score.");
    }

    @Override
    public void printDetails() { System.out.println(getRegistrationSummary()); }

    @Override
    public String getRegistrationSummary() {
        return String.format("Assessment [%s] | Type: %s | Score: %.1f / %.1f", assessmentId, assessmentType, obtainedScore, maximumScore);
    }
}