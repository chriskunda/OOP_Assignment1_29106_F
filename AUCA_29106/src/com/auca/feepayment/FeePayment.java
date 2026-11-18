package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    public Student getStudent() { return student; }
    public String getReceiptNumber() { return receiptNumber; }

    @Override
    public String getId() { return paymentId; }
    @Override
    public String getDisplayName() { return "Receipt " + receiptNumber + " (" + amount + ")"; }
    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        if (paymentId == null || paymentId.isBlank()) throw new ValidationException("Payment ID cannot be blank.");
        if (student == null) throw new ValidationException("Student must be specified.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new ValidationException("Amount must be strictly positive.");
        if (receiptNumber == null || receiptNumber.isBlank()) throw new ValidationException("Receipt number cannot be empty.");
    }

    @Override
    public void printDetails() { System.out.println(getRegistrationSummary()); }

    @Override
    public String getRegistrationSummary() {
        return String.format("Payment [%s] | Student: %s | Amount: %s | Receipt: %s", paymentId, student.getDisplayName(), amount, receiptNumber);
    }
}