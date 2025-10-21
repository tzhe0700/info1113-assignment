import java.util.*;
import java.io.*;

public class Archive {
    private Map<Integer, Student> students = new HashMap<>();
    private Map<Integer, SpellBook> spellBooks = new HashMap<>();
    private int nextStudentNumber = 100000;

    public static void main(String[] args) {
        Archive archive = new Archive();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine();
            if (input.trim().equalsIgnoreCase("EXIT")) {
                System.out.println("Ending Archive process.");
                break;
            } else {
                archive.processCommand(input);
            }
        }
    }

    public void processCommand(String input) {
        String original = input;
        input = input.trim();
        
        // Handle simple commands first
        if (input.equalsIgnoreCase("COMMANDS")) {
            List<String> output = new ArrayList<>();
            output.addAll(getCommandsOutput());
            printUserOutput(output);
            return;
        }
        
        String upper = input.toUpperCase();
        
        // Route to appropriate handler
        if (upper.startsWith("ADD STUDENT ")) {
            handleAddStudent(original);
        } else if (upper.startsWith("ADD SPELLBOOK ")) {
            handleAddSpellbook(original);
        } else if (upper.startsWith("ADD COLLECTION ")) {
            handleAddCollection(original);
        } else if (upper.equals("LIST ALL") || upper.equals("LIST ALL LONG")) {
            handleListAll(upper.endsWith("LONG"));
        } else if (upper.equals("LIST AVAILABLE") || upper.equals("LIST AVAILABLE LONG")) {
            handleListAvailable(upper.endsWith("LONG"));
        } else if (upper.equals("NUMBER COPIES")) {
            handleNumberCopies();
        } else if (upper.equals("LIST TYPES")) {
            handleListTypes();
        } else if (upper.equals("LIST INVENTORS")) {
            handleListInventors();
        } else if (upper.startsWith("TYPE ")) {
            handleType(original);
        } else if (upper.startsWith("INVENTOR ")) {
            handleInventor(original);
        } else if (upper.startsWith("SPELLBOOK ")) {
            handleSpellbook(original, upper);
        } else if (upper.startsWith("SPELLBOOK HISTORY ")) {
            handleSpellbookHistory(original);
        } else if (upper.startsWith("STUDENT ")) {
            handleStudent(original);
        } else if (upper.startsWith("STUDENT SPELLBOOKS ")) {
            handleStudentSpellbooks(original);
        } else if (upper.startsWith("STUDENT HISTORY ")) {
            handleStudentHistory(original);
        } else if (upper.startsWith("RENT ")) {
            handleRent(original);
        } else if (upper.startsWith("RELINQUISH ALL ")) {
            handleRelinquishAll(original);
        } else if (upper.startsWith("RELINQUISH ")) {
            handleRelinquish(original);
        } else if (upper.startsWith("SAVE COLLECTION ")) {
            handleSaveCollection(original);
        } else if (upper.startsWith("COMMON ")) {
            handleCommon(original);
        }
        // Invalid commands are ignored per spec
    }

    // Command handlers
    private void handleAddStudent(String original) {
        String name = original.substring(12).trim();
        if (name.isEmpty()) {
            printUserOutput("No name provided.");
            return;
        }
        int studentNum = nextStudentNumber++;
        students.put(studentNum, new Student(studentNum, name));
        printUserOutput("Success.");
    }

    private void handleAddSpellbook(String original) {
        String[] parts = original.split(" ", 4);
        if (parts.length < 4) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String filename = parts[2];
        try {
            File f = new File(filename);
            if (!f.exists()) {
                printUserOutput("No such file.");
                return;
            }
        } catch (Exception e) {
            printUserOutput("No such file.");
            return;
        }
        int serial;
        try {
            serial = Integer.parseInt(parts[3].trim());
        } catch (Exception e) {
            printUserOutput("No such spellbook in file.");
            return;
        }
        SpellBook toAdd = loadSpellBookBySerial(filename, serial);
        if (spellBooks.containsKey(serial)) {
            printUserOutput("Spellbook already exists in system.");
            return;
        }
        if (toAdd == null) {
            printUserOutput("No such spellbook in file.");
            return;
        }
        spellBooks.put(serial, toAdd);
        printUserOutput("Successfully added: " + toAdd.toShortString() + ".");
    }

    private void handleAddCollection(String original) {
        String[] parts = original.split(" ", 3);
        if (parts.length < 3) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String filename = parts[2];
        try {
            File f = new File(filename);
            if (!f.exists()) {
                printUserOutput("No such collection.");
                return;
            }
        } catch (Exception e) {
            printUserOutput("No such collection.");
            return;
        }
        int added = 0;
        for (SpellBook s : loadSpellBooks(filename)) {
            if (!spellBooks.containsKey(s.getSerialNumber())) {
                spellBooks.put(s.getSerialNumber(), s);
                added++;
            }
        }
        if (added == 0) {
            printUserOutput("No spellbooks have been added to the system.");
        } else {
            printUserOutput(added + " spellbooks successfully added.");
        }
    }

    private void handleListAll(boolean isLong) {
        List<SpellBook> books = new ArrayList<>(spellBooks.values());
        books.sort(Comparator.comparingInt(SpellBook::getSerialNumber));
        if (books.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        List<String> output = new ArrayList<>();
        boolean firstSpellbook = true;
        for (SpellBook sb : books) {
            if (isLong) {
                if (!firstSpellbook) {
                    output.add(""); // Add blank line between spellbooks
                }
                String longStr = sb.toLongString();
                String[] lines = longStr.split("\n");
                for (String line : lines) {
                    output.add(line);
                }
                firstSpellbook = false;
            } else {
                output.add(sb.toShortString());
            }
        }
        printUserOutput(output);
    }

    private void handleListAvailable(boolean isLong) {
        List<SpellBook> books = new ArrayList<>(spellBooks.values());
        books.sort(Comparator.comparingInt(SpellBook::getSerialNumber));
        if (books.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        boolean anyAvailable = false;
        List<String> output = new ArrayList<>();
        boolean firstSpellbook = true;
        for (SpellBook sb : books) {
            if (!sb.isAvailable()) continue;
            anyAvailable = true;
            if (isLong) {
                if (!firstSpellbook) {
                    output.add(""); // Add blank line between spellbooks
                }
                String longStr = sb.toLongString();
                String[] lines = longStr.split("\n");
                for (String line : lines) {
                    output.add(line);
                }
                firstSpellbook = false;
            } else {
                output.add(sb.toShortString());
            }
        }
        if (!anyAvailable) {
            printUserOutput("No spellbooks available.");
            return;
        }
        printUserOutput(output);
    }

    private void handleNumberCopies() {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        Map<String, Integer> map = new TreeMap<>();
        for (SpellBook sb : spellBooks.values()) {
            String key = sb.getTitle() + " (" + sb.getInventor() + ")";
            map.put(key, map.getOrDefault(key, 0) + 1);
        }
        List<String> output = new ArrayList<>();
        for (Map.Entry<String, Integer> e : map.entrySet()) {
            output.add(e.getKey() + ": " + e.getValue());
        }
        printUserOutput(output);
    }

    private void handleListTypes() {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        Set<String> types = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (SpellBook sb : spellBooks.values()) types.add(sb.getType());
        if (types.isEmpty()) {
            printUserOutput("No spellbooks in system.");
        } else {
            List<String> typeList = new ArrayList<>();
            for (String t : new TreeSet<>(types)) typeList.add(capFirst(t));
            printUserOutput(typeList);
        }
    }

    private void handleListInventors() {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        Set<String> inventors = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (SpellBook sb : spellBooks.values()) inventors.add(sb.getInventor());
        if (inventors.isEmpty()) {
            printUserOutput("No spellbooks in system.");
        } else {
            List<String> inventorList = new ArrayList<>();
            for (String t : new TreeSet<>(inventors)) inventorList.add(capFirst(t));
            printUserOutput(inventorList);
        }
    }

    private void handleType(String original) {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String type = original.substring(5).trim();
        List<SpellBook> found = new ArrayList<>();
        for (SpellBook sb : spellBooks.values()) {
            if (sb.getType().equalsIgnoreCase(type)) found.add(sb);
        }
        if (found.isEmpty()) {
            printUserOutput("No spellbooks with type " + type + ".");
            return;
        }
        found.sort(Comparator.comparing(SpellBook::getTitle));
        List<String> output = new ArrayList<>();
        for (SpellBook sb : found) output.add(sb.toShortString());
        printUserOutput(output);
    }

    private void handleInventor(String original) {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String inventor = original.substring(9).trim();
        List<SpellBook> found = new ArrayList<>();
        for (SpellBook sb : spellBooks.values()) {
            if (sb.getInventor().equalsIgnoreCase(inventor)) found.add(sb);
        }
        if (found.isEmpty()) {
            printUserOutput("No spellbooks by " + inventor + ".");
            return;
        }
        found.sort(Comparator.comparing(SpellBook::getTitle));
        List<String> output = new ArrayList<>();
        for (SpellBook sb : found) output.add(sb.toShortString());
        printUserOutput(output);
    }

    private void handleSpellbook(String original, String upper) {
        String[] args = original.split(" ");
        if (args.length >= 3 && args[2].equalsIgnoreCase("LONG")) {
            try {
                int serial = Integer.parseInt(args[1]);
                SpellBook sb = spellBooks.get(serial);
                if (sb == null) {
                    if (spellBooks.isEmpty()) {
                        printUserOutput("No spellbooks in system.");
                    } else {
                        printUserOutput("No such spellbook in system.");
                    }
                    return;
                }
                printUserOutput(sb.toLongString());
                return;
            } catch (Exception e) { 
                if (spellBooks.isEmpty()) {
                    printUserOutput("No spellbooks in system.");
                } else {
                    printUserOutput("No such spellbook in system.");
                }
                return; 
            }
        } else if (args.length >= 2) {
            try {
                int serial = Integer.parseInt(args[1]);
                SpellBook sb = spellBooks.get(serial);
                if (sb == null) {
                    if (spellBooks.isEmpty()) {
                        printUserOutput("No spellbooks in system.");
                    } else {
                        printUserOutput("No such spellbook in system.");
                    }
                    return;
                }
                printUserOutput(sb.toShortString());
                return;
            } catch (Exception e) { 
                if (spellBooks.isEmpty()) {
                    printUserOutput("No spellbooks in system.");
                } else {
                    printUserOutput("No such spellbook in system.");
                }
                return; 
            }
        }
    }

    private void handleSpellbookHistory(String original) {
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("No such spellbook in system.");
            return;
        }
        try {
            int serial = Integer.parseInt(args[2]);
            SpellBook sb = spellBooks.get(serial);
            if (sb == null) {
                printUserOutput("No such spellbook in system.");
                return;
            }
            List<Integer> hist = sb.getRentalHistory();
            if (hist.isEmpty()) {
                printUserOutput("No rental history.");
                return;
            }
            List<String> hLines = new ArrayList<>();
            for (int num : hist) hLines.add(num + "");
            printUserOutput(hLines);
        } catch (Exception e) { 
            printUserOutput("No such spellbook in system."); 
        }
    }

    private void handleStudent(String original) {
        String[] args = original.split(" ");
        if (args.length < 2) {
            if (students.isEmpty()) printUserOutput("No students in system.");
            else printUserOutput("No such student in system.");
            return;
        }
        try {
            int num = Integer.parseInt(args[1]);
            Student stu = students.get(num);
            if (stu == null) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            printUserOutput(stu.toString());
        } catch (Exception e) { 
            printUserOutput("No such student in system."); 
        }
    }

    private void handleStudentSpellbooks(String original) {
        if (students.isEmpty()) {
            printUserOutput("No students in system.");
            return;
        }
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("No such student in system.");
            return;
        }
        try {
            int num = Integer.parseInt(args[2]);
            Student stu = students.get(num);
            if (stu == null) {
                printUserOutput("No such student in system.");
                return;
            }
            if (!stu.isCurrentlyRenting()) {
                printUserOutput("Student not currently renting.");
            } else {
                List<String> lines = new ArrayList<>();
                for (int sn : stu.getCurrentRentals()) {
                    SpellBook sb = spellBooks.get(sn);
                    if (sb != null) lines.add(sb.toShortString());
                }
                printUserOutput(lines);
            }
        } catch (Exception e) { 
            printUserOutput("No such student in system."); 
        }
    }

    private void handleStudentHistory(String original) {
        if (students.isEmpty()) {
            printUserOutput("No students in system.");
            return;
        }
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("No such student in system.");
            return;
        }
        try {
            int num = Integer.parseInt(args[2]);
            Student stu = students.get(num);
            if (stu == null) {
                printUserOutput("No such student in system.");
                return;
            }
            if (stu.getRentalHistory().isEmpty()) {
                printUserOutput("No rental history for student.");
            } else {
                List<String> list = new ArrayList<>();
                for (int sn : stu.getRentalHistory()) {
                    SpellBook sb = spellBooks.get(sn);
                    if (sb != null) list.add(sb.toShortString());
                }
                printUserOutput(list);
            }
        } catch (Exception e) { 
            printUserOutput("No such student in system."); 
        }
    }

    private void handleRent(String original) {
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("No students in system.");
            return;
        }
        try {
            int snum = Integer.parseInt(args[1]);
            int serial = Integer.parseInt(args[2]);
            Student stu = students.get(snum);
            if (stu == null) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            SpellBook sb = spellBooks.get(serial);
            if (sb == null) {
                if (spellBooks.isEmpty()) printUserOutput("No spellbooks in system.");
                else printUserOutput("No such spellbook in system.");
                return;
            }
            if (!sb.isAvailable()) {
                printUserOutput("Spellbook is currently unavailable.");
                return;
            }
            sb.rentTo(snum);
            stu.rentSpellbook(serial);
            printUserOutput("Success.");
        } catch (Exception e) { 
            if (students.isEmpty()) printUserOutput("No students in system.");
            else printUserOutput("No such student in system.");
        }
    }

    private void handleRelinquishAll(String original) {
        String[] args = original.split(" ");
        if (args.length < 3) {
            if (students.isEmpty()) printUserOutput("No students in system.");
            else printUserOutput("No such student in system.");
            return;
        }
        try {
            int snum = Integer.parseInt(args[2]);
            Student stu = students.get(snum);
            if (stu == null) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            if (!stu.isCurrentlyRenting()) {
                printUserOutput("Student not currently renting.");
                return;
            }
            for (int sn : new ArrayList<>(stu.getCurrentRentals())) {
                SpellBook sb = spellBooks.get(sn);
                if (sb != null && sb.getCurrentRenter() != null && sb.getCurrentRenter() == snum) {
                    sb.relinquishFrom(snum);
                    stu.returnSpellbook(sn);
                }
            }
            printUserOutput("Success.");
        } catch (Exception e) { 
            if (students.isEmpty()) printUserOutput("No students in system.");
            else printUserOutput("No such student in system.");
        }
    }

    private void handleRelinquish(String original) {
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("No students in system.");
            return;
        }
        try {
            int snum = Integer.parseInt(args[1]);
            int serial = Integer.parseInt(args[2]);
            Student stu = students.get(snum);
            if (stu == null) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            SpellBook sb = spellBooks.get(serial);
            if (sb == null) {
                if (spellBooks.isEmpty()) printUserOutput("No spellbooks in system.");
                else printUserOutput("No such spellbook in system.");
                return;
            }
            if (sb.getCurrentRenter() == null || sb.getCurrentRenter() != snum) {
                printUserOutput("Unable to return spellbook.");
                return;
            }
            sb.relinquishFrom(snum);
            stu.returnSpellbook(serial);
            printUserOutput("Success.");
        } catch (Exception e) { 
            if (students.isEmpty()) printUserOutput("No students in system.");
            else printUserOutput("No such student in system.");
        }
    }

    private void handleSaveCollection(String original) {
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String[] args = original.split(" ");
        if (args.length < 3) {
            printUserOutput("Unable to save collection.");
            return;
        }
        String filename = args[2];
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            List<SpellBook> sbs = new ArrayList<>(spellBooks.values());
            sbs.sort(Comparator.comparingInt(SpellBook::getSerialNumber));
            pw.println("serialNumber,title,inventor,type");
            for (SpellBook sb : sbs) {
                pw.printf("%d,%s,%s,%s\n", sb.getSerialNumber(), sb.getTitle(), sb.getInventor(), sb.getType());
            }
            printUserOutput("Success.");
        } catch (IOException e) {
            printUserOutput("Unable to save collection.");
        }
    }

    private void handleCommon(String original) {
        if (students.isEmpty()) {
            printUserOutput("No students in system.");
            return;
        }
        if (spellBooks.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        String[] args = original.split(" ");
        Set<Integer> nums = new LinkedHashSet<>();
        for (int i = 1; i < args.length; i++) {
            try {
                int num = Integer.parseInt(args[i]);
                if (nums.contains(num)) {
                    printUserOutput("Duplicate students provided.");
                    return;
                }
                nums.add(num);
            } catch (Exception e) {
                printUserOutput("No such student in system.");
                return;
            }
        }
        if (nums.size() < 2) {
            printUserOutput("No common spellbooks.");
            return;
        }
        List<Set<Integer>> histories = new ArrayList<>();
        for (int n : nums) {
            Student s = students.get(n);
            if (s == null) {
                printUserOutput("No such student in system.");
                return;
            }
            histories.add(new HashSet<>(s.getRentalHistory()));
        }
        if (histories.isEmpty()) {
            printUserOutput("No spellbooks in system.");
            return;
        }
        Set<Integer> common = new HashSet<>(histories.get(0));
        for (int i = 1; i < histories.size(); i++) {
            common.retainAll(histories.get(i));
        }
        if (common.isEmpty()) {
            printUserOutput("No common spellbooks.");
            return;
        }
        List<String> commonTitles = new ArrayList<>();
        for (int sn : common) {
            SpellBook sb = spellBooks.get(sn);
            if (sb != null) commonTitles.add(sb.toShortString());
        }
        Collections.sort(commonTitles);
        printUserOutput(commonTitles);
    }

    // Helper methods
    private void printUserOutput(String line) {
        System.out.println(line);
        System.out.println();
    }
    
    private void printUserOutput(List<String> lines) {
        if (lines.isEmpty()) {
            System.out.println();
            return;
        }
        System.out.println(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            System.out.println(lines.get(i));
        }
        System.out.println();
    }
    
    private List<String> getCommandsOutput() {
        List<String> help = new ArrayList<>();
        help.add("EXIT ends the archive process");
        help.add("COMMANDS outputs this help string");
        help.add("");

        help.add("LIST ALL [LONG] outputs either the short or long string for all spellbooks");
        help.add("LIST AVAILABLE [LONG] outputs either the short or long string for all available spellbooks");
        help.add("NUMBER COPIES outputs the number of copies of each spellbook");
        help.add("LIST TYPES outputs the name of every type in the system");
        help.add("LIST INVENTORS outputs the name of every inventor in the system");
        help.add("");

        help.add("TYPE <type> outputs the short string of every spellbook with the specified type");
        help.add("INVENTOR <inventor> outputs the short string of every spellbook by the specified inventor");
        help.add("");

        help.add("SPELLBOOK <serialNumber> [LONG] outputs either the short or long string for the specified spellbook");
        help.add("SPELLBOOK HISTORY <serialNumber> outputs the rental history of the specified spellbook");
        help.add("");

        help.add("STUDENT <studentNumber> outputs the information of the specified student");
        help.add("STUDENT SPELLBOOKS <studentNumber> outputs the spellbooks currently rented by the specified student");
        help.add("STUDENT HISTORY <studentNumber> outputs the rental history of the specified student");
        help.add("");

        help.add("RENT <studentNumber> <serialNumber> loans out the specified spellbook to the given student");
        help.add("RELINQUISH <studentNumber> <serialNumber> returns the specified spellbook from the student");
        help.add("RELINQUISH ALL <studentNumber> returns all spellbooks rented by the specified student");
        help.add("");

        help.add("ADD STUDENT <name> adds a student to the system");
        help.add("ADD SPELLBOOK <filename> <serialNumber> adds a spellbook to the system");
        help.add("");

        help.add("ADD COLLECTION <filename> adds a collection of spellbooks to the system");
        help.add("SAVE COLLECTION <filename> saves the system to a csv file");
        help.add("");

        help.add("COMMON <studentNumber1> <studentNumber2> ... outputs the common spellbooks in students' history");
        return help;
    }

    private String capFirst(String s) {
        if (s == null || s.length() == 0) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private SpellBook loadSpellBookBySerial(String filename, int serial) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine && line.toLowerCase().contains("serialnumber")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;
                String[] cols = line.split(",");
                if (cols.length < 4) continue;
                int sn = Integer.parseInt(cols[0]);
                if (sn == serial) {
                    return new SpellBook(
                        sn, cols[1], cols[2], cols[3]
                    );
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private List<SpellBook> loadSpellBooks(String filename) {
        List<SpellBook> books = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine && line.toLowerCase().contains("serialnumber")) {
                    isFirstLine = false;
                    continue;
                }
                isFirstLine = false;
                String[] cols = line.split(",");
                if (cols.length < 4) continue;
                int sn = Integer.parseInt(cols[0]);
                books.add(new SpellBook(sn, cols[1], cols[2], cols[3]));
            }
        } catch (Exception ignored) {}
        return books;
    }
}
