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
                System.out.println("user: Ending Archive process.");
                break;
            } else {
                archive.processCommand(input);
            }
        }
    }

    public void processCommand(String input) {
        String original = input;
        input = input.trim();
        List<String> output = new ArrayList<>();
        if (input.equalsIgnoreCase("COMMANDS")) {
            output.addAll(getCommandsOutput());
            printUserOutput(output);
            return;
        }
        String[] tokens = input.split(" ");
        String upper = input.toUpperCase();
        if (upper.startsWith("ADD STUDENT ")) {
            String name = original.substring(12).trim();
            // Collapse internal multiple spaces to single spaces for canonical storage,
            // but preserve leading/trailing trim as per examples
            name = name.replaceAll("\\s+", " ");
            if (name.isEmpty()) {
                printUserOutput("No name provided.");
                return;
            }
            int studentNum = nextStudentNumber++;
            students.put(studentNum, new Student(studentNum, name));
            printUserOutput("Success.");
            return;
        }
        if (upper.startsWith("ADD SPELLBOOK ")) {
            String[] parts = original.split(" ", 4);
            if (parts.length < 4) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            String filename = parts[2];
            // If file does not exist, report as such
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
            return;
        }
        if (upper.startsWith("ADD COLLECTION ")) {
            String[] parts = original.split(" ", 3);
            if (parts.length < 3) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            String filename = parts[2];
            // If file does not exist, report as such
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
            return;
        }
        if (upper.equals("LIST ALL") || upper.equals("LIST ALL LONG") 
            || upper.equals("LIST AVAILABLE") || upper.equals("LIST AVAILABLE LONG")) {
            boolean isAll = upper.startsWith("LIST ALL");
            boolean isAvailable = upper.startsWith("LIST AVAILABLE");
            boolean isLong = upper.endsWith("LONG");
            List<SpellBook> books = new ArrayList<>(spellBooks.values());
            books.sort(Comparator.comparingInt(SpellBook::getSerialNumber));
            if (books.isEmpty()) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            boolean anyAvailable = false;
            boolean firstSpellbook = true;
            for (SpellBook sb : books) {
                if (isAvailable && !sb.isAvailable()) continue;
                if (isAvailable) anyAvailable = true;
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
            if (isAvailable && !anyAvailable) {
                printUserOutput("No spellbooks available.");
                return;
            }
            printUserOutput(output);
            return;
        }
        if (upper.equals("LIST TYPES")) {
            if (spellBooks.isEmpty()) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            Map<String, String> typeMap = new LinkedHashMap<>();
            for (SpellBook sb : spellBooks.values()) {
                String t = sb.getType();
                String key = t.toLowerCase();
                if (!typeMap.containsKey(key)) typeMap.put(key, t);
            }
            if (typeMap.isEmpty()) {
                printUserOutput("No spellbooks in system.");
            } else {
                List<String> keys = new ArrayList<>(typeMap.keySet());
                keys.sort(String.CASE_INSENSITIVE_ORDER);
                List<String> out = new ArrayList<>();
                for (String k : keys) out.add(typeMap.get(k));
                printUserOutput(out);
            }
            return;
        }
        if (upper.equals("LIST INVENTORS")) {
            if (spellBooks.isEmpty()) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            Map<String, String> inventorMap = new LinkedHashMap<>();
            for (SpellBook sb : spellBooks.values()) {
                String inv = sb.getInventor();
                String key = inv.toLowerCase();
                if (!inventorMap.containsKey(key)) inventorMap.put(key, inv);
            }
            if (inventorMap.isEmpty()) {
                printUserOutput("No spellbooks in system.");
            } else {
                List<String> keys = new ArrayList<>(inventorMap.keySet());
                keys.sort(String.CASE_INSENSITIVE_ORDER);
                List<String> out = new ArrayList<>();
                for (String k : keys) out.add(inventorMap.get(k));
                printUserOutput(out);
            }
            return;
        }
        if (upper.startsWith("TYPE ")) {
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
            for (SpellBook sb : found) output.add(sb.toShortString());
            printUserOutput(output);
            return;
        }
        if (upper.startsWith("INVENTOR ")) {
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
            for (SpellBook sb : found) output.add(sb.toShortString());
            printUserOutput(output);
            return;
        }
        if (upper.equals("NUMBER COPIES")) {
            if (spellBooks.isEmpty()) {
                printUserOutput("No spellbooks in system.");
                return;
            }
            Map<String, Integer> map = new TreeMap<>();
            for (SpellBook sb : spellBooks.values()) {
                String key = sb.getTitle()+ " (" + sb.getInventor() + ")";
                map.put(key, map.getOrDefault(key, 0) + 1);
            }
            for (Map.Entry<String, Integer> e : map.entrySet()) {
                output.add(e.getKey() + ": " + e.getValue());
            }
            printUserOutput(output);
            return;
        }
        if (upper.startsWith("SPELLBOOK HISTORY ")) {
            // Correct handling for: SPELLBOOK HISTORY <serialNumber>
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
                for (int num : hist) hLines.add(num+"");
                printUserOutput(hLines);
                return;
            } catch (Exception e) { printUserOutput("No such spellbook in system."); return; }
        }
        if (upper.startsWith("SPELLBOOK ")) {
            String[] args = original.split(" ");
            // Support alternate: SPELLBOOK <serial> HISTORY
            if (args.length >= 3 && args[2].equalsIgnoreCase("HISTORY")) {
                try {
                    int serial = Integer.parseInt(args[1]);
                    SpellBook sb = spellBooks.get(serial);
                    if (sb == null) { printUserOutput("No such spellbook in system."); return; }
                    List<Integer> hist = sb.getRentalHistory();
                    if (hist.isEmpty()) { printUserOutput("No rental history."); return; }
                    List<String> hLines = new ArrayList<>();
                    for (int num : hist) hLines.add(num+"");
                    printUserOutput(hLines);
                    return;
                } catch (Exception e) { printUserOutput("No such spellbook in system."); return; }
            }
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
        if (upper.startsWith("STUDENT HISTORY ")) {
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
                return;
            } catch (Exception e) { printUserOutput("No such student in system."); return; }
        }
        if (upper.startsWith("STUDENT SPELLBOOKS ")) {
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
                return;
            } catch (Exception e) { printUserOutput("No such student in system."); return; }
        }
        if (upper.startsWith("STUDENT ")) {
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
                return;
            } catch (Exception e) { printUserOutput("No such student in system."); return; }
        }
        if (upper.startsWith("RENT ")) {
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
                return;
            } catch (Exception e) { 
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return; 
            }
        }
        if (upper.startsWith("RELINQUISH ALL ")) {
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
                return;
            } catch (Exception e) { 
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return; 
            }
        }
        if (upper.startsWith("RELINQUISH ")) {
            String[] args = original.split(" ");
            if (args.length < 3) {
                printUserOutput("No students in system.");
                return;
            }
            int snum;
            try {
                snum = Integer.parseInt(args[1]);
            } catch (Exception e) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            Student stu = students.get(snum);
            if (stu == null) {
                if (students.isEmpty()) printUserOutput("No students in system.");
                else printUserOutput("No such student in system.");
                return;
            }
            int serial;
            try {
                serial = Integer.parseInt(args[2]);
            } catch (Exception e) {
                if (spellBooks.isEmpty()) printUserOutput("No spellbooks in system.");
                else printUserOutput("No such spellbook in system.");
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
            return;
        }
        if (upper.startsWith("ADD ")) {
            // Ignore invalid ADD commands per spec
            return;
        }
        if (upper.startsWith("SAVE COLLECTION ")) {
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
                // Write CSV header as expected by tests
                pw.println("serialNumber,title,inventor,type");
                for (SpellBook sb : sbs) {
                    pw.printf("%d,%s,%s,%s\n", sb.getSerialNumber(), sb.getTitle(), sb.getInventor(), sb.getType());
                }
                printUserOutput("Success.");
            } catch (IOException e) {
                printUserOutput("Unable to save collection.");
            }
            return;
        }
        if (upper.startsWith("COMMON ")) {
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
            return;
        }
        // If nothing matched and not an edge error, ignore.
    }

    // Helper to output one or many lines grouped with blank after
    private void printUserOutput(String line) {
        System.out.println("user: " + line);
        System.out.println();
    }
    private void printUserOutput(List<String> lines) {
        if (lines.isEmpty()) {
            System.out.println();
            return;
        }
        System.out.println("user: " + lines.get(0));
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
