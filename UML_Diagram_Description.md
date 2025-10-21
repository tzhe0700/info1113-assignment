# Hogwarts Archive System - UML Class Diagram

## Class Diagram Description for draw.io

### Classes and Their Relationships:

#### 1. Archive Class
- **Attributes:**
  - `Map<Integer, Student> students` (private)
  - `Map<Integer, SpellBook> spellBooks` (private)
  - `int nextStudentNumber` (private)

- **Methods:**
  - `main(String[] args)` (public static)
  - `processCommand(String input)` (public)
  - `printUserOutput(String line)` (private)
  - `printUserOutput(List<String> lines)` (private)
  - `getCommandsOutput()` (private)
  - `capFirst(String s)` (private)
  - `loadSpellBookBySerial(String filename, int serial)` (private)
  - `loadSpellBooks(String filename)` (private)

#### 2. Student Class
- **Attributes:**
  - `int studentNumber` (private)
  - `String name` (private)
  - `List<Integer> currentRentals` (private)
  - `List<Integer> rentalHistory` (private)

- **Methods:**
  - `Student(int studentNumber, String name)` (constructor)
  - `getStudentNumber()` (public)
  - `getName()` (public)
  - `getCurrentRentals()` (public)
  - `getRentalHistory()` (public)
  - `rentSpellbook(int serialNumber)` (public)
  - `returnSpellbook(int serialNumber)` (public)
  - `returnAllSpellbooks()` (public)
  - `isCurrentlyRenting()` (public)
  - `toString()` (public)

#### 3. SpellBook Class
- **Attributes:**
  - `int serialNumber` (private)
  - `String title` (private)
  - `String inventor` (private)
  - `String type` (private)
  - `Integer currentRenter` (private)
  - `List<Integer> rentalHistory` (private)

- **Methods:**
  - `SpellBook(int serialNumber, String title, String inventor, String type)` (constructor)
  - `getSerialNumber()` (public)
  - `getTitle()` (public)
  - `getInventor()` (public)
  - `getType()` (public)
  - `getCurrentRenter()` (public)
  - `getRentalHistory()` (public)
  - `isAvailable()` (public)
  - `rentTo(int studentNumber)` (public)
  - `relinquishFrom(int studentNumber)` (public)
  - `isCopyOf(SpellBook other)` (public)
  - `toShortString()` (public)
  - `toLongString()` (public)

### Relationships:

1. **Archive → Student**: One-to-Many (1..*)
   - Archive manages multiple Student objects
   - Relationship: "manages"

2. **Archive → SpellBook**: One-to-Many (1..*)
   - Archive manages multiple SpellBook objects
   - Relationship: "manages"

3. **Student ↔ SpellBook**: Many-to-Many (through rental system)
   - Students can rent multiple SpellBooks
   - SpellBooks can be rented by different Students over time
   - Relationship: "rents" (temporary association)

### Key Design Patterns:

1. **Command Pattern**: Archive class processes various commands
2. **Data Transfer Objects**: Student and SpellBook are simple data containers
3. **Repository Pattern**: Archive acts as a repository for Students and SpellBooks

### Multiplicity:
- Archive (1) manages (0..*) Student
- Archive (1) manages (0..*) SpellBook
- Student (0..*) rents (0..*) SpellBook (temporary relationship)

### Notes:
- The rental relationship is temporary and tracked through currentRenter and currentRentals
- History is maintained separately in both Student and SpellBook classes
- The system uses serial numbers and student numbers as unique identifiers
