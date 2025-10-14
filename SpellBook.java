import java.util.*;

public class SpellBook {
    private int serialNumber;
    private String title;
    private String inventor;
    private String type;
    private Integer currentRenter; // student number or null
    private List<Integer> rentalHistory;

    public SpellBook(int serialNumber, String title, String inventor, String type) {
        this.serialNumber = serialNumber;
        this.title = title;
        this.inventor = inventor;
        this.type = type;
        this.currentRenter = null;
        this.rentalHistory = new ArrayList<>();
    }

    public int getSerialNumber() { return serialNumber; }
    public String getTitle() { return title; }
    public String getInventor() { return inventor; }
    public String getType() { return type; }
    public Integer getCurrentRenter() { return currentRenter; }
    public List<Integer> getRentalHistory() { return rentalHistory; }

    public boolean isAvailable() { return currentRenter == null; }
    public void rentTo(int studentNumber) { this.currentRenter = studentNumber; }
    public boolean relinquishFrom(int studentNumber) {
        if (currentRenter != null && currentRenter == studentNumber) {
            rentalHistory.add(studentNumber);
            currentRenter = null;
            return true;
        }
        return false;
    }
    public boolean isCopyOf(SpellBook other) {
        return this.title.equals(other.title) && this.inventor.equals(other.inventor);
    }

    // Short form: Title (Inventor)
    public String toShortString() {
        return String.format("%s (%s)", title, inventor);
    }
    // Long form: serial: Title (Inventor, Type), plus status
    public String toLongString() {
        String base = String.format("%d: %s (%s, %s)", serialNumber, title, inventor, type);
        String status = isAvailable() ? "Currently available." : String.format("Rented by: %d.", currentRenter);
        return base + "\n" + status;
    }
}
