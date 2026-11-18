package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Classroom implements Identifiable, Registrable {
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber, int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override
    public String getId() { return classroomId; }
    @Override
    public String getDisplayName() { return buildingName + " Room " + roomNumber; }
    @Override
    public boolean isActive() { return available; }

    @Override
    public void validate() throws ValidationException {
        if (classroomId == null || classroomId.isBlank()) throw new ValidationException("Classroom ID cannot be blank.");
        if (capacity <= 0) throw new ValidationException("Capacity must be greater than zero.");
        if (floorNumber < 0) throw new ValidationException("Floor number must be zero or greater.");
    }

    @Override
    public void printDetails() { System.out.println(getRegistrationSummary()); }

    @Override
    public String getRegistrationSummary() {
        return String.format("[%s] Building: %s, Room: %s (Cap: %d, Floor: %d)", classroomId, buildingName, roomNumber, capacity, floorNumber);
    }

    @Override
    public String toString() { return "[" + classroomId + "] " + buildingName + " " + roomNumber; }
}