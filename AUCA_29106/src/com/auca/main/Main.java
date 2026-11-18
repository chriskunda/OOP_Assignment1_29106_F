package com.auca.main;

import com.auca.exceptions.*;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

import java.util.List;
import java.util.Scanner;

public class Main { 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SchoolManagementService service = new SchoolManagementService(scanner);

        int choice = -1;
        while (choice != 0) {
            System.out.println("\n==== AUCA_ID SCHOOL MANAGEMENT ====");
            System.out.println("1. Register Department         11. Borrow Library Book");
            System.out.println("2. Register Student            12. Return Library Book");
            System.out.println("3. Register Teacher            13. View All Records");
            System.out.println("4. Register Administrator      14. Search Record by ID");
            System.out.println("5. Register Course             15. Update Existing Record");
            System.out.println("6. Register Classroom          16. Student Academic Summary");
            System.out.println("7. Enroll Student              17. Student Payment History");
            System.out.println("8. Record Assessment           18. View Available Books");
            System.out.println("9. Record Fee Payment          19. Demonstrate Polymorphism");
            System.out.println("10. Register Library Book      0. Exit");
            System.out.print("Select an option: ");

            String rawInput = scanner.nextLine().trim();
            try {
                choice = Integer.parseInt(rawInput);
            } catch (NumberFormatException e) {
                System.out.println("! Invalid input. Please enter a number between 0 and 19.");
                continue;
            }

            try {
                switch (choice) {
                    case 1 -> service.registerDepartment();
                    case 2 -> service.registerStudent();
                    case 3 -> service.registerTeacher();
                    case 4 -> service.registerAdministrator();
                    case 5 -> service.registerCourse();
                    case 6 -> service.registerClassroom();
                    case 7 -> service.enrollStudent();
                    case 8 -> service.recordAssessment();
                    case 9 -> service.recordFeePayment();
                    case 10 -> service.registerLibraryBook();
                    case 11 -> service.borrowBook();
                    case 12 -> service.returnBook();
                    case 13 -> {
                        System.out.println("\n=== ALL REGISTERED RECORDS ===");
                        List<Identifiable> all = service.getAllIdentifiables();
                        if (all.isEmpty()) {
                            System.out.println("No records registered yet.");
                        } else {
                            for (Identifiable item : all) {
                                if (item instanceof Registrable r) r.printDetails();
                            }
                        }
                    }
                    case 14 -> service.searchById();
                    case 15 -> service.updateRecord();
                    case 16 -> service.showStudentAcademicSummary();
                    case 17 -> service.showPaymentHistory();
                    case 18 -> service.showAvailableBooks();
                    case 19 -> service.demonstratePolymorphism();
                    case 0 -> System.out.println("Exiting AUCA_ID. Goodbye!");
                    default -> System.out.println("! Out of range. Please choose an option from 0 to 19.");
                }
            } catch (DuplicateRecordException | RecordNotFoundException | PrerequisiteMissingException | BookUnavailableException e) {
                System.out.println("! Operation failed: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("! Unexpected error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}