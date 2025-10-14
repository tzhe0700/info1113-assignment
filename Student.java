import java.util.*;

public class Student {
    private int studentNumber;
    private String name;
    private List<Integer> currentRentals; // serial numbers of books rented
    private List<Integer> rentalHistory;   // serial numbers in chronological order

    public Student(int studentNumber, String name) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.currentRentals = new ArrayList<>();
        this.rentalHistory = new ArrayList<>();
    }

    public int getStudentNumber() { return studentNumber; }
    public String getName() { return name; }

    public List<Integer> getCurrentRentals() { return currentRentals; }
    public List<Integer> getRentalHistory() { return rentalHistory; }

    public void rentSpellbook(int serialNumber) {
        currentRentals.add(serialNumber);
    }
    public boolean returnSpellbook(int serialNumber) {
        if (currentRentals.remove((Integer)serialNumber)) {
            rentalHistory.add(serialNumber);
            return true;
        }
        return false;
    }
    public void returnAllSpellbooks() {
        for (int serialNumber : new ArrayList<>(currentRentals)) {
            returnSpellbook(serialNumber);
        }
    }
    public boolean isCurrentlyRenting() {
        return !currentRentals.isEmpty();
    }
    public String toString() {
        return String.format("%d: %s", studentNumber, name);
    }
}
