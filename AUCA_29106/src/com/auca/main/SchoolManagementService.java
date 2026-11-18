package com.auca.main;

import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.*;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SchoolManagementService {
    private final Scanner scanner;

    public final List<Department> departments = new ArrayList<>();
    public final List<Student> students = new ArrayList<>();
    public final List<Teacher> teachers = new ArrayList<>();
    public final List<Administrator> administrators = new ArrayList<>();
    public final List<Course> courses = new ArrayList<>();
    public final List<Classroom> classrooms = new ArrayList<>();
    public final List<Enrollment> enrollments = new ArrayList<>();
    public final List<Assessment> assessments = new ArrayList<>();
    public final List<FeePayment> feePayments = new ArrayList<>();
    public final List<LibraryBook> libraryBooks = new ArrayList<>();
    public final List<BookLoan> bookLoans = new ArrayList<>();

    private static class BackSignal extends RuntimeException {}
    private static class CancelSignal extends RuntimeException {}

    public SchoolManagementService(Scanner scanner) {
        this.scanner = scanner;
    }

    
    private String readLine(String prompt, boolean allowBack) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("CANCEL")) throw new CancelSignal();
        if (allowBack && input.equalsIgnoreCase("BACK")) throw new BackSignal();
        return input;
    }

    private String promptText(String label, int min, int max, boolean allowBack) {
        while (true) {
            String val = readLine(label + ": ", allowBack);
            if (val.length() < min || val.length() > max) {
                System.out.println("! Must be between " + min + " and " + max + " characters.");
                continue;
            }
            return val;
        }
    }

    private String promptUniqueId(String label, boolean allowBack) {
        while (true) {
            String id = promptText(label, 4, 30, allowBack);
            if (findById(id) != null) {
                System.out.println("! ID '" + id + "' already exists. Enter a unique ID.");
                continue;
            }
            return id;
        }
    }

    private String promptEmail(String label, boolean checkUniquePersonEmail, boolean allowBack) {
        while (true) {
            String email = readLine(label + ": ", allowBack);
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$") || email.length() > 120) {
                System.out.println("! Invalid email syntax or length (max 120 chars).");
                continue;
            }
            if (checkUniquePersonEmail) {
                boolean exists = students.stream().anyMatch(s -> s.getEmail().equalsIgnoreCase(email))
                        || teachers.stream().anyMatch(t -> t.getEmail().equalsIgnoreCase(email))
                        || administrators.stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email));
                if (exists) {
                    System.out.println("! Email '" + email + "' is already registered.");
                    continue;
                }
            }
            return email;
        }
    }

    private String promptPhone(String label, boolean allowBack) {
        while (true) {
            String phone = readLine(label + ": ", allowBack);
            if (!phone.matches("^\\+?[0-9]{10,15}$")) {
                System.out.println("! Phone must be 10-15 digits (optional leading +).");
                continue;
            }
            return phone;
        }
    }

    private LocalDate promptDate(String label, boolean mustBePast, boolean allowFuture, boolean allowBack) {
        while (true) {
            String raw = readLine(label + " [YYYY-MM-DD]: ", allowBack);
            try {
                LocalDate date = LocalDate.parse(raw);
                if (mustBePast && !date.isBefore(LocalDate.now())) {
                    System.out.println("! Date must be in the past.");
                    continue;
                }
                if (!allowFuture && date.isAfter(LocalDate.now())) {
                    System.out.println("! Date cannot be in the future.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2004-05-16).");
            }
        }
    }

    private int promptInt(String label, int min, int max, boolean allowBack) {
        while (true) {
            String raw = readLine(label + ": ", allowBack);
            try {
                int val = Integer.parseInt(raw);
                if (val < min || val > max) {
                    System.out.println("! Enter a value between " + min + " and " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("! Invalid integer. Please try again.");
            }
        }
    }

    private double promptDouble(String label, double min, double max, boolean allowBack) {
        while (true) {
            String raw = readLine(label + ": ", allowBack);
            try {
                double val = Double.parseDouble(raw);
                if (val < min || val > max) {
                    System.out.println("! Enter a value between " + min + " and " + max + ".");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("! Invalid decimal number.");
            }
        }
    }

    private BigDecimal promptPositiveMoney(String label, boolean allowBack) {
        while (true) {
            String raw = readLine(label + ": ", allowBack);
            try {
                BigDecimal val = new BigDecimal(raw);
                if (val.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("! Amount must be strictly greater than 0.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("! Invalid monetary amount.");
            }
        }
    }

    private String promptChoice(String label, String[] allowed, boolean allowBack) {
        while (true) {
            String val = readLine(label + " (" + String.join("/", allowed) + "): ", allowBack).toUpperCase();
            for (String opt : allowed) {
                if (opt.equalsIgnoreCase(val)) return opt;
            }
            System.out.println("! Invalid option. Allowed values: " + String.join(", ", allowed));
        }
    }

    // ==================== 1. DEPARTMENT REGISTRATION ====================

    public Department registerDepartment() {
        try {
            System.out.println("\n=== REGISTER DEPARTMENT (Type CANCEL anytime to abort) ===");
            String id = promptUniqueId("01 Department ID [unique, 4-30 chars]", false);
            String name = promptText("02 Department Name [2-60 chars]", 2, 60, false);
            String code;
            while (true) {
                code = promptText("03 Department Code [2-20 chars]", 2, 20, false);
                final String checkCode = code;
                if (departments.stream().anyMatch(d -> d.getDepartmentCode().equalsIgnoreCase(checkCode))) {
                    System.out.println("! Department code already exists.");
                    continue;
                }
                break;
            }
            String office = promptText("04 Office Location [2-60 chars]", 2, 60, false);
            String email = promptEmail("05 Contact Email", false, false);

            Teacher head = null;
            if (!teachers.isEmpty()) {
                System.out.println("06 Select Head Teacher (or 0 to leave unassigned):");
                for (int i = 0; i < teachers.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + teachers.get(i).getRegistrationSummary());
                }
                int pick = promptInt("Select option", 0, teachers.size(), false);
                if (pick > 0) head = teachers.get(pick - 1);
            } else {
                System.out.println("06 Head Teacher: [None available - unassigned at creation]");
            }

            while (true) {
                System.out.println("\nREVIEW DEPARTMENT:");
                System.out.println("01 ID: " + id + " | 02 Name: " + name + " | 03 Code: " + code);
                System.out.println("04 Office: " + office + " | 05 Email: " + email + " | 06 Head: " + (head == null ? "None" : head.getDisplayName()));
                int action = promptInt("[1] Save  [2] Edit Field  [0] Cancel\nSelect option", 0, 2, false);
                if (action == 0) {
                    System.out.println("! Department registration cancelled.");
                    return null;
                } else if (action == 2) {
                    int f = promptInt("Enter field number (1-5)", 1, 5, false);
                    if (f == 1) id = promptUniqueId("New Department ID", false);
                    else if (f == 2) name = promptText("New Department Name", 2, 60, false);
                    else if (f == 3) code = promptText("New Department Code", 2, 20, false);
                    else if (f == 4) office = promptText("New Office Location", 2, 60, false);
                    else if (f == 5) email = promptEmail("New Contact Email", false, false);
                } else {
                    Department dept = new Department(id, name, code, office, email, head);
                    dept.validate();
                    departments.add(dept);
                    System.out.println("+ Department " + id + " registered successfully.");
                    return dept;
                }
            }
        } catch (CancelSignal e) {
            System.out.println("! Department registration cancelled.");
            return null;
        } catch (ValidationException e) {
            System.out.println("! Validation failed: " + e.getMessage());
            return null;
        }
    }

    // ==================== 2. SMART 4-STEP STUDENT REGISTRATION ====================

    public Student registerStudent() {
        String personId = null, firstName = null, lastName = null, email = null, phone = null;
        LocalDate dob = null;
        String memberNo = null, academicStatus = null, instEmail = null, advisorName = null;
        Department dept = null;
        LocalDate joinDate = null;
        String studentNo = null, programName = null, guardianPhone = null;
        LocalDate admissionDate = null;
        int studyYear = 1;
        boolean scholarship = false;

        int step = 1;
        try {
            while (step <= 4) {
                System.out.println("\n==================================================");
                System.out.println("  AUCA_ID SCHOOL MANAGEMENT - STUDENT REGISTRATION");
                System.out.println("  Step " + step + " of 4  |  Type BACK or CANCEL anytime");
                System.out.println("==================================================");

                try {
                    if (step == 1) {
                        System.out.println("[STEP 1/4] PERSONAL DETAILS");
                        personId = promptUniqueId("01 Person ID [unique, 4-30 chars]", false);
                        firstName = promptText("02 First name [2-60 chars]", 2, 60, false);
                        lastName = promptText("03 Last name [2-60 chars]", 2, 60, false);
                        email = promptEmail("04 Email [unique]", true, false);
                        phone = promptPhone("05 Phone [10-15 digits]", false);
                        dob = promptDate("06 Date of birth", true, false, false);
                        step = 2;
                    } else if (step == 2) {
                        System.out.println("[STEP 2/4] ACADEMIC DETAILS");
                        memberNo = promptText("07 Member number [unique, 4-30 chars]", 4, 30, true);
                        dept = selectOrCreateDepartment();
                        if (dept == null) throw new CancelSignal();
                        joinDate = promptDate("09 Join date", false, false, true);
                        int st = promptInt("10 Academic status [1 ACTIVE / 2 INACTIVE]", 1, 2, true);
                        academicStatus = (st == 1) ? "ACTIVE" : "INACTIVE";
                        instEmail = promptEmail("11 Institutional email", false, true);
                        advisorName = promptText("12 Advisor name [2-120 chars]", 2, 120, true);
                        step = 3;
                    } else if (step == 3) {
                        System.out.println("[STEP 3/4] STUDENT DETAILS");
                        while (true) {
                            studentNo = promptText("13 Student number [unique, 4-30 chars]", 4, 30, true);
                            final String checkStu = studentNo;
                            if (students.stream().anyMatch(s -> s.getStudentNumber().equalsIgnoreCase(checkStu))) {
                                System.out.println("! Student number " + studentNo + " already exists.");
                                continue;
                            }
                            break;
                        }
                        programName = promptText("14 Program name [2-120 chars]", 2, 120, true);
                        admissionDate = promptDate("15 Admission date", false, false, true);
                        studyYear = promptInt("16 Study year [1-6]", 1, 6, true);
                        guardianPhone = promptPhone("17 Guardian phone [10-15 digits]", true);
                        int sch = promptInt("18 Scholarship [1 YES / 2 NO]", 1, 2, true);
                        scholarship = (sch == 1);
                        step = 4;
                    } else if (step == 4) {
                        printStudentReview(personId, firstName, lastName, email, phone, dob,
                                memberNo, dept, joinDate, academicStatus, instEmail, advisorName,
                                studentNo, programName, admissionDate, studyYear, guardianPhone, scholarship);

                        int action = promptInt("[1] Save student  [2] Edit field  [0] Cancel\nSelect option", 0, 2, true);
                        if (action == 0) {
                            throw new CancelSignal();
                        } else if (action == 2) {
                            int field = promptInt("Enter field number to edit (01-18)", 1, 18, false);
                            switch (field) {
                                case 1 -> { System.out.println("Old: " + personId); personId = promptUniqueId("01 Person ID", false); }
                                case 2 -> { System.out.println("Old: " + firstName); firstName = promptText("02 First name", 2, 60, false); }
                                case 3 -> { System.out.println("Old: " + lastName); lastName = promptText("03 Last name", 2, 60, false); }
                                case 4 -> { System.out.println("Old: " + email); email = promptEmail("04 Email", true, false); }
                                case 5 -> { System.out.println("Old: " + phone); phone = promptPhone("05 Phone", false); }
                                case 6 -> { System.out.println("Old: " + dob); dob = promptDate("06 Date of birth", true, false, false); }
                                case 7 -> { System.out.println("Old: " + memberNo); memberNo = promptText("07 Member number", 4, 30, false); }
                                case 8 -> { System.out.println("Old: " + dept); Department d = selectOrCreateDepartment(); if (d != null) dept = d; }
                                case 9 -> { System.out.println("Old: " + joinDate); joinDate = promptDate("09 Join date", false, false, false); }
                                case 10 -> { System.out.println("Old: " + academicStatus); academicStatus = (promptInt("10 Status [1 ACTIVE / 2 INACTIVE]", 1, 2, false) == 1) ? "ACTIVE" : "INACTIVE"; }
                                case 11 -> { System.out.println("Old: " + instEmail); instEmail = promptEmail("11 Institutional email", false, false); }
                                case 12 -> { System.out.println("Old: " + advisorName); advisorName = promptText("12 Advisor name", 2, 120, false); }
                                case 13 -> { System.out.println("Old: " + studentNo); studentNo = promptText("13 Student number", 4, 30, false); }
                                case 14 -> { System.out.println("Old: " + programName); programName = promptText("14 Program name", 2, 120, false); }
                                case 15 -> { System.out.println("Old: " + admissionDate); admissionDate = promptDate("15 Admission date", false, false, false); }
                                case 16 -> { System.out.println("Old: " + studyYear); studyYear = promptInt("16 Study year [1-6]", 1, 6, false); }
                                case 17 -> { System.out.println("Old: " + guardianPhone); guardianPhone = promptPhone("17 Guardian phone", false); }
                                case 18 -> { System.out.println("Old: " + (scholarship ? "YES" : "NO")); scholarship = (promptInt("18 Scholarship [1 YES / 2 NO]", 1, 2, false) == 1); }
                            }
                        } else {
                            Student student = new Student(personId, firstName, lastName, email, phone, dob,
                                    memberNo, dept, joinDate, academicStatus, instEmail, advisorName,
                                    studentNo, programName, admissionDate, studyYear, guardianPhone, scholarship);
                            student.validate();
                            students.add(student);
                            System.out.println("+ Student " + studentNo + " registered successfully.");
                            return student;
                        }
                    }
                } catch (BackSignal b) {
                    if (step > 1) step--;
                }
            }
        } catch (CancelSignal c) {
            System.out.println("! Student registration cancelled. No record saved.");
        } catch (ValidationException e) {
            System.out.println("! Validation error: " + e.getMessage());
        }
        return null;
    }

    private Department selectOrCreateDepartment() {
        while (true) {
            System.out.println("\nCHOOSE DEPARTMENT:");
            if (departments.isEmpty()) {
                System.out.println("  ! No Department is registered yet.");
                System.out.println("  1 [+] Register a new Department");
                System.out.println("  0 Cancel");
                int pick = promptInt("Select option", 0, 1, false);
                if (pick == 0) return null;
                Department created = registerDepartment();
                if (created != null) {
                    System.out.println("Department saved. Returning to previous form...");
                    return created;
                }
            } else {
                for (int i = 0; i < departments.size(); i++) {
                    System.out.println("  " + (i + 1) + " " + departments.get(i).toString());
                }
                int regOpt = departments.size() + 1;
                System.out.println("  " + regOpt + " [+] Register a new Department");
                System.out.println("  0 Cancel");
                int pick = promptInt("Select option", 0, regOpt, false);
                if (pick == 0) return null;
                if (pick == regOpt) {
                    Department created = registerDepartment();
                    if (created != null) return created;
                } else {
                    return departments.get(pick - 1);
                }
            }
        }
    }

    private void printStudentReview(String personId, String firstName, String lastName, String email, String phone, LocalDate dob,
                                    String memberNo, Department dept, LocalDate joinDate, String academicStatus, String instEmail, String advisorName,
                                    String studentNo, String programName, LocalDate admissionDate, int studyYear, String guardianPhone, boolean scholarship) {
        System.out.println("\n--- REVIEW STUDENT (ALL 18 FIELDS) ---");
        System.out.println("01 Person ID          : " + personId);
        System.out.println("02 First name         : " + firstName);
        System.out.println("03 Last name          : " + lastName);
        System.out.println("04 Email              : " + email);
        System.out.println("05 Phone              : " + phone);
        System.out.println("06 Date of birth      : " + dob);
        System.out.println("07 Member number      : " + memberNo);
        System.out.println("08 Department         : " + dept);
        System.out.println("09 Join date          : " + joinDate);
        System.out.println("10 Academic status    : " + academicStatus);
        System.out.println("11 Institutional email: " + instEmail);
        System.out.println("12 Advisor name       : " + advisorName);
        System.out.println("13 Student number     : " + studentNo);
        System.out.println("14 Program name       : " + programName);
        System.out.println("15 Admission date     : " + admissionDate);
        System.out.println("16 Study year         : " + studyYear);
        System.out.println("17 Guardian phone     : " + guardianPhone);
        System.out.println("18 Scholarship        : " + (scholarship ? "YES" : "NO"));
    }

    // ==================== 3. REGISTER TEACHER ====================

    public Teacher registerTeacher() {
        try {
            System.out.println("\n=== REGISTER TEACHER ===");
            Department dept = selectOrCreateDepartment();
            if (dept == null) return null;

            String pId = promptUniqueId("01 Person ID", false);
            String fName = promptText("02 First name", 2, 60, false);
            String lName = promptText("03 Last name", 2, 60, false);
            String email = promptEmail("04 Personal email", true, false);
            String phone = promptPhone("05 Phone", false);
            LocalDate dob = promptDate("06 Date of birth", true, false, false);
            String mNo = promptText("07 Member number", 4, 30, false);
            LocalDate joinDate = promptDate("08 Join date", false, false, false);
            String status = promptChoice("09 Academic status", new String[]{"ACTIVE", "INACTIVE"}, false);
            String instEmail = promptEmail("10 Institutional email", false, false);
            String advisor = promptText("11 Advisor/Supervisor name", 2, 120, false);

            String empNo = promptText("12 Employee number [unique]", 4, 30, false);
            String spec = promptText("13 Specialization", 2, 80, false);
            LocalDate hireDate = promptDate("14 Hire date", false, false, false);
            BigDecimal salary = promptPositiveMoney("15 Salary", false);
            String office = promptText("16 Office number", 1, 30, false);
            String empType = promptChoice("17 Employment type", new String[]{"FULL_TIME", "PART_TIME", "VISITING"}, false);

            Teacher teacher = new Teacher(pId, fName, lName, email, phone, dob, mNo, dept, joinDate, status, instEmail, advisor,
                    empNo, spec, hireDate, salary, office, empType);
            teacher.validate();
            teachers.add(teacher);
            System.out.println("+ Teacher " + empNo + " registered successfully.");
            return teacher;
        } catch (CancelSignal e) {
            System.out.println("! Teacher registration cancelled.");
            return null;
        } catch (ValidationException e) {
            System.out.println("! Validation failed: " + e.getMessage());
            return null;
        }
    }

    // ==================== 4. REGISTER ADMINISTRATOR ====================

    public Administrator registerAdministrator() {
        try {
            System.out.println("\n=== REGISTER ADMINISTRATOR ===");
            String pId = promptUniqueId("01 Person ID", false);
            String fName = promptText("02 First name", 2, 60, false);
            String lName = promptText("03 Last name", 2, 60, false);
            String email = promptEmail("04 Email", true, false);
            String phone = promptPhone("05 Phone", false);
            LocalDate dob = promptDate("06 Date of birth", true, false, false);

            String staffNo = promptText("07 Staff number", 4, 30, false);
            String jobTitle = promptText("08 Job title", 2, 60, false);
            String office = promptText("09 Assigned office", 1, 30, false);
            LocalDate empDate = promptDate("10 Employment date", false, false, false);
            int access = promptInt("11 Access level [1-5]", 1, 5, false);
            boolean active = promptInt("12 Active [1 YES / 2 NO]", 1, 2, false) == 1;

            Administrator admin = new Administrator(pId, fName, lName, email, phone, dob, staffNo, jobTitle, office, empDate, access, active);
            admin.validate();
            administrators.add(admin);
            System.out.println("+ Administrator " + staffNo + " registered successfully.");
            return admin;
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! Cancelled or invalid: " + e.getMessage());
            return null;
        }
    }

    // ==================== 5. REGISTER COURSE ====================

    public Course registerCourse() {
        try {
            System.out.println("\n=== REGISTER COURSE ===");
            Department dept = selectOrCreateDepartment();
            if (dept == null) return null;

            Teacher teacher;
            while (true) {
                if (teachers.isEmpty()) {
                    System.out.println("! No Teacher is registered. A Course requires an assigned Teacher.");
                    int opt = promptInt("1. Register Teacher now  0. Cancel\nSelect", 0, 1, false);
                    if (opt == 0) return null;
                    Teacher t = registerTeacher();
                    if (t != null) { teacher = t; break; }
                } else {
                    System.out.println("Select Assigned Teacher:");
                    for (int i = 0; i < teachers.size(); i++) {
                        System.out.println("  " + (i + 1) + ". " + teachers.get(i).getRegistrationSummary());
                    }
                    int opt = promptInt("Select (or 0 to cancel)", 0, teachers.size(), false);
                    if (opt == 0) return null;
                    teacher = teachers.get(opt - 1);
                    break;
                }
            }

            String cId = promptUniqueId("Course ID", false);
            String cCode = promptText("Course Code [2-20 chars]", 2, 20, false);
            String cTitle = promptText("Course Title [2-60 chars]", 2, 60, false);
            int credits = promptInt("Credit Hours [1-6]", 1, 6, false);

            Course course = new Course(cId, cCode, cTitle, credits, dept, teacher);
            course.validate();
            courses.add(course);
            System.out.println("+ Course " + cCode + " registered successfully.");
            return course;
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
            return null;
        }
    }

    // ==================== 6. REGISTER CLASSROOM ====================

    public void registerClassroom() {
        try {
            System.out.println("\n=== REGISTER CLASSROOM ===");
            String id = promptUniqueId("Classroom ID", false);
            String bldg = promptText("Building Name", 2, 60, false);
            String room = promptText("Room Number", 1, 20, false);
            int cap = promptInt("Capacity [> 0]", 1, 1000, false);
            int floor = promptInt("Floor Number [>= 0]", 0, 100, false);
            boolean avail = promptInt("Available [1 YES / 2 NO]", 1, 2, false) == 1;

            Classroom c = new Classroom(id, bldg, room, cap, floor, avail);
            c.validate();
            classrooms.add(c);
            System.out.println("+ Classroom " + id + " registered successfully.");
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    // ==================== 7. ENROLL STUDENT ====================

    public Enrollment enrollStudent() throws DuplicateRecordException {
        try {
            System.out.println("\n=== ENROLL STUDENT ===");
            while (students.isEmpty()) {
                System.out.println("! No Student exists. Register a student before enrolling.");
                int opt = promptInt("1. Register Student now  0. Cancel\nSelect", 0, 1, false);
                if (opt == 0) return null;
                registerStudent();
            }
            System.out.println("Select Student:");
            for (int i = 0; i < students.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + students.get(i).getRegistrationSummary());
            }
            int sPick = promptInt("Select (0 to cancel)", 0, students.size(), false);
            if (sPick == 0) return null;
            Student student = students.get(sPick - 1);

            while (courses.isEmpty()) {
                System.out.println("! No Course exists. Register a course before enrolling a student.");
                int opt = promptInt("1. Register Course now  0. Cancel\nSelect", 0, 1, false);
                if (opt == 0) return null;
                registerCourse();
            }
            System.out.println("Select Course:");
            for (int i = 0; i < courses.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + courses.get(i).getRegistrationSummary());
            }
            int cPick = promptInt("Select (0 to cancel)", 0, courses.size(), false);
            if (cPick == 0) return null;
            Course course = courses.get(cPick - 1);

            String eId = promptUniqueId("Enrollment ID", false);
            LocalDate eDate = promptDate("Enrollment Date", false, false, false);
            String semester = promptChoice("Semester", new String[]{"SEM1", "SEM2", "SUMMER"}, false);
            String status = promptChoice("Status", new String[]{"ACTIVE", "DROPPED", "COMPLETED"}, false);

            for (Enrollment existing : enrollments) {
                if (existing.getStudent().getId().equals(student.getId())
                        && existing.getCourse().getId().equals(course.getId())
                        && existing.getSemester().equalsIgnoreCase(semester)
                        && existing.isActive()) {
                    throw new DuplicateRecordException("Student already has an ACTIVE enrollment in " + course.getCourseCode() + " for " + semester);
                }
            }

            Enrollment enr = new Enrollment(eId, student, course, eDate, semester, status);
            enr.validate();
            enrollments.add(enr);
            System.out.println("+ Enrollment " + eId + " saved successfully.");
            return enr;
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
            return null;
        }
    }

    // ==================== 8. RECORD ASSESSMENT ====================

    public void recordAssessment() throws PrerequisiteMissingException {
        try {
            System.out.println("\n=== RECORD ASSESSMENT ===");
            List<Enrollment> activeEnrollments = enrollments.stream().filter(Enrollment::isActive).toList();
            if (activeEnrollments.isEmpty()) {
                throw new PrerequisiteMissingException("No ACTIVE enrollments found. Enroll a student first.");
            }
            System.out.println("Select Active Enrollment:");
            for (int i = 0; i < activeEnrollments.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + activeEnrollments.get(i).getRegistrationSummary());
            }
            int pick = promptInt("Select (0 to cancel)", 0, activeEnrollments.size(), false);
            if (pick == 0) return;
            Enrollment selected = activeEnrollments.get(pick - 1);

            String aId = promptUniqueId("Assessment ID", false);
            String type = promptChoice("Assessment Type", new String[]{"QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL"}, false);
            double maxScore = promptDouble("Maximum Score [> 0]", 0.1, 1000.0, false);
            double obtained = promptDouble("Obtained Score [0 - " + maxScore + "]", 0.0, maxScore, false);
            LocalDate aDate = promptDate("Assessment Date", false, false, false);

            Assessment a = new Assessment(aId, selected, type, maxScore, obtained, aDate);
            a.validate();
            assessments.add(a);
            System.out.println("+ Assessment " + aId + " recorded successfully.");
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    // ==================== 9. RECORD FEE PAYMENT ====================

    public void recordFeePayment() throws PrerequisiteMissingException, DuplicateRecordException {
        try {
            System.out.println("\n=== RECORD FEE PAYMENT ===");
            if (students.isEmpty()) {
                throw new PrerequisiteMissingException("No students registered. Register a student before recording a payment.");
            }
            for (int i = 0; i < students.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + students.get(i).getRegistrationSummary());
            }
            int pick = promptInt("Select Student (0 to cancel)", 0, students.size(), false);
            if (pick == 0) return;
            Student stu = students.get(pick - 1);

            String pId = promptUniqueId("Payment ID", false);
            BigDecimal amount = promptPositiveMoney("Payment Amount", false);
            LocalDate pDate = promptDate("Payment Date", false, false, false);
            String method = promptChoice("Payment Method", new String[]{"CASH", "BANK_TRANSFER", "MOBILE_MONEY", "CARD"}, false);
            String receipt = promptText("Receipt Number [unique]", 3, 40, false);

            for (FeePayment fp : feePayments) {
                if (fp.getReceiptNumber().equalsIgnoreCase(receipt)) {
                    throw new DuplicateRecordException("Receipt number '" + receipt + "' already exists.");
                }
            }

            FeePayment payment = new FeePayment(pId, stu, amount, pDate, method, receipt);
            payment.validate();
            feePayments.add(payment);
            System.out.println("+ Payment " + pId + " recorded successfully.");
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    // ==================== 10, 11, 12. LIBRARY & BOOK LOANS ====================

    public LibraryBook registerLibraryBook() {
        try {
            System.out.println("\n=== REGISTER LIBRARY BOOK ===");
            String id = promptUniqueId("Book ID", false);
            String isbn = promptText("ISBN", 5, 30, false);
            String title = promptText("Book Title", 2, 100, false);
            String author = promptText("Author", 2, 60, false);
            int total = promptInt("Total Copies [>= 0]", 0, 10000, false);
            int avail = promptInt("Available Copies [0 - " + total + "]", 0, total, false);

            LibraryBook book = new LibraryBook(id, isbn, title, author, total, avail);
            book.validate();
            libraryBooks.add(book);
            System.out.println("+ Library book " + id + " registered successfully.");
            return book;
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
            return null;
        }
    }

    public void borrowBook() throws PrerequisiteMissingException, BookUnavailableException {
        try {
            System.out.println("\n=== BORROW LIBRARY BOOK ===");
            if (students.isEmpty()) throw new PrerequisiteMissingException("No Student registered.");
            if (libraryBooks.isEmpty()) throw new PrerequisiteMissingException("No Library Book registered.");

            System.out.println("Select Student:");
            for (int i = 0; i < students.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + students.get(i).getRegistrationSummary());
            }
            int sPick = promptInt("Select (0 to cancel)", 0, students.size(), false);
            if (sPick == 0) return;
            Student student = students.get(sPick - 1);

            System.out.println("Select Book:");
            for (int i = 0; i < libraryBooks.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + libraryBooks.get(i).getRegistrationSummary());
            }
            int bPick = promptInt("Select (0 to cancel)", 0, libraryBooks.size(), false);
            if (bPick == 0) return;
            LibraryBook book = libraryBooks.get(bPick - 1);

            if (book.getAvailableCopies() <= 0) {
                throw new BookUnavailableException("No copies available for '" + book.getTitle() + "'.");
            }

            String loanId = promptUniqueId("Loan ID", false);
            LocalDate borrowed = promptDate("Borrowed Date", false, false, false);
            LocalDate due;
            while (true) {
                due = promptDate("Due Date (must be after " + borrowed + ")", false, true, false);
                if (!due.isAfter(borrowed)) {
                    System.out.println("! Due date must be strictly after borrowed date.");
                    continue;
                }
                break;
            }

            BookLoan loan = new BookLoan(loanId, student, book, borrowed, due, null);
            loan.validate();

            int confirm = promptInt("Confirm loan? [1] Yes  [0] Cancel\nSelect", 0, 1, false);
            if (confirm == 1) {
                bookLoans.add(loan);
                book.setAvailableCopies(book.getAvailableCopies() - 1);
                System.out.println("+ Book borrowed! Remaining copies of '" + book.getTitle() + "': " + book.getAvailableCopies());
            } else {
                System.out.println("! Loan cancelled. Stock unchanged.");
            }
        } catch (CancelSignal | ValidationException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    public void returnBook() throws RecordNotFoundException {
        try {
            System.out.println("\n=== RETURN LIBRARY BOOK ===");
            List<BookLoan> activeLoans = bookLoans.stream().filter(BookLoan::isActive).toList();
            if (activeLoans.isEmpty()) {
                throw new RecordNotFoundException("No active book loans to return.");
            }
            for (int i = 0; i < activeLoans.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + activeLoans.get(i).getRegistrationSummary());
            }
            int pick = promptInt("Select loan to return (0 to cancel)", 0, activeLoans.size(), false);
            if (pick == 0) return;
            BookLoan loan = activeLoans.get(pick - 1);

            LocalDate retDate;
            while (true) {
                retDate = promptDate("Return Date (on or after " + loan.getBorrowedDate() + ")", false, false, false);
                if (retDate.isBefore(loan.getBorrowedDate())) {
                    System.out.println("! Return date cannot be before borrowed date (" + loan.getBorrowedDate() + ").");
                    continue;
                }
                break;
            }

            loan.setReturnedDate(retDate);
            LibraryBook book = loan.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            System.out.println("+ Book returned! Available copies of '" + book.getTitle() + "': " + book.getAvailableCopies());
        } catch (CancelSignal e) {
            System.out.println("! Return cancelled.");
        }
    }

    // ==================== 14–18. SEARCH, UPDATE & REPORTS ====================

    public Identifiable findById(String id) {
        List<Identifiable> all = getAllIdentifiables();
        for (Identifiable item : all) {
            if (item.getId().equalsIgnoreCase(id)) return item;
        }
        return null;
    }

    public List<Identifiable> getAllIdentifiables() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(feePayments);
        all.addAll(libraryBooks);
        all.addAll(bookLoans);
        return all;
    }

    public void searchById() throws RecordNotFoundException {
        System.out.print("Enter ID to search: ");
        String id = scanner.nextLine().trim();
        Identifiable found = findById(id);
        if (found == null) {
            throw new RecordNotFoundException("No record found with ID: " + id);
        }
        if (found instanceof Registrable r) {
            r.printDetails();
        } else {
            System.out.println("Found: " + found.getDisplayName());
        }
    }

    public void updateRecord() throws RecordNotFoundException {
        System.out.print("Enter ID of record to update (Student phone or Department office): ");
        String id = scanner.nextLine().trim();
        Identifiable found = findById(id);
        if (found == null) throw new RecordNotFoundException("Record not found: " + id);

        if (found instanceof Student s) {
            System.out.println("Current: " + s.getRegistrationSummary());
            String newPhone = promptPhone("Enter new phone number", false);
            int confirm = promptInt("Confirm update? [1] Yes [0] No", 0, 1, false);
            if (confirm == 1) {
                s.setPhoneNumber(newPhone);
                System.out.println("+ Student updated.");
            }
        } else if (found instanceof Department d) {
            System.out.println("Current: " + d.getRegistrationSummary());
            String newOffice = promptText("Enter new office location", 2, 60, false);
            int confirm = promptInt("Confirm update? [1] Yes [0] No", 0, 1, false);
            if (confirm == 1) {
                d.setOfficeLocation(newOffice);
                System.out.println("+ Department updated.");
            }
        } else {
            System.out.println("Record found: " + found.getDisplayName() + " (Editable fields configured for Student/Department).");
        }
    }

    public void showStudentAcademicSummary() throws RecordNotFoundException {
        if (students.isEmpty()) throw new RecordNotFoundException("No students registered.");
        for (int i = 0; i < students.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + students.get(i).getRegistrationSummary());
        }
        int pick = promptInt("Select Student (0 to cancel)", 0, students.size(), false);
        if (pick == 0) return;
        Student s = students.get(pick - 1);

        System.out.println("\n=== ACADEMIC SUMMARY FOR " + s.getDisplayName().toUpperCase() + " ===");
        s.printDetails();
        System.out.println("Enrollments:");
        enrollments.stream().filter(e -> e.getStudent().getId().equals(s.getId()))
                .forEach(e -> System.out.println("  - " + e.getRegistrationSummary()));
    }

    public void showPaymentHistory() throws RecordNotFoundException {
        if (students.isEmpty()) throw new RecordNotFoundException("No students registered.");
        for (int i = 0; i < students.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + students.get(i).getRegistrationSummary());
        }
        int pick = promptInt("Select Student (0 to cancel)", 0, students.size(), false);
        if (pick == 0) return;
        Student s = students.get(pick - 1);

        System.out.println("\n=== PAYMENT HISTORY FOR " + s.getDisplayName().toUpperCase() + " ===");
        feePayments.stream().filter(p -> p.getStudent().getId().equals(s.getId()))
                .forEach(p -> System.out.println("  - " + p.getRegistrationSummary()));
    }

    public void showAvailableBooks() {
        System.out.println("\n=== AVAILABLE LIBRARY BOOKS ===");
        List<LibraryBook> avail = libraryBooks.stream().filter(b -> b.getAvailableCopies() > 0).toList();
        if (avail.isEmpty()) {
            System.out.println("No books currently available.");
        } else {
            avail.forEach(LibraryBook::printDetails);
        }
    }

    public void demonstratePolymorphism() {
        System.out.println("\n=== POLYMORPHISM DEMONSTRATION ===");
        List<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);

        System.out.println("1) Superclass Polymorphism (List<Person>):");
        if (people.isEmpty()) System.out.println("  [Register a Student, Teacher, or Admin first to see output]");
        for (Person p : people) {
            System.out.println("  - " + p.getDisplayName() + " => Role: " + p.getRoleDescription());
        }

        System.out.println("\n2) Interface Polymorphism (List<Identifiable>):");
        List<Identifiable> items = getAllIdentifiables();
        if (items.isEmpty()) System.out.println("  [No entities registered yet]");
        for (Identifiable item : items) {
            System.out.println("  - ID: " + item.getId() + " | Name: " + item.getDisplayName() + " | Active: " + item.isActive());
        }
    }
}