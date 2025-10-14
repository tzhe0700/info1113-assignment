# Hogwarts Archive - INFO1113 Assignment

## Overview
This Java application implements a Hogwarts Archive system that manages student accounts and spellbook rentals. The system allows users to add students and spellbooks, rent and return spellbooks, and query various information about the archive.

## Files Structure
- `Archive.java` - Main application class that handles command processing
- `Student.java` - Student class representing student accounts
- `SpellBook.java` - SpellBook class representing individual spellbooks
- `spellbooks.csv` - Sample CSV file with spellbook data
- `tests/` - Directory containing comprehensive test cases

## Key Features
- **Student Management**: Add students with auto-incrementing student numbers (starting from 100000)
- **Spellbook Management**: Add individual spellbooks or entire collections from CSV files
- **Rental System**: Students can rent and return spellbooks with proper history tracking
- **Query Commands**: Various commands to list, search, and analyze spellbooks and students
- **Data Persistence**: Save collections to CSV files

## Commands Supported
- `EXIT` - Ends the archive process
- `COMMANDS` - Shows help information
- `ADD STUDENT <name>` - Adds a new student
- `ADD SPELLBOOK <filename> <serialNumber>` - Adds a single spellbook
- `ADD COLLECTION <filename>` - Adds all spellbooks from a CSV file
- `LIST ALL [LONG]` - Lists all spellbooks
- `LIST AVAILABLE [LONG]` - Lists available spellbooks
- `RENT <studentNumber> <serialNumber>` - Rents a spellbook to a student
- `RELINQUISH <studentNumber> <serialNumber>` - Returns a spellbook
- `RELINQUISH ALL <studentNumber>` - Returns all spellbooks for a student
- `STUDENT <studentNumber>` - Shows student information
- `STUDENT SPELLBOOKS <studentNumber>` - Shows currently rented spellbooks
- `STUDENT HISTORY <studentNumber>` - Shows rental history
- `SPELLBOOK <serialNumber> [LONG]` - Shows spellbook information
- `SPELLBOOK HISTORY <serialNumber>` - Shows rental history
- `TYPE <type>` - Lists spellbooks by type
- `INVENTOR <inventor>` - Lists spellbooks by inventor
- `LIST TYPES` - Lists all types
- `LIST INVENTORS` - Lists all inventors
- `NUMBER COPIES` - Shows number of copies for each spellbook
- `COMMON <studentNumber1> <studentNumber2> ...` - Shows common spellbooks in history
- `SAVE COLLECTION <filename>` - Saves current collection to CSV

## Test Cases
The `tests/` directory contains comprehensive test cases covering:
- Basic functionality for each command
- Edge cases and error handling
- Complex scenarios with multiple students and spellbooks
- All examples from the assignment specification

## Usage
1. Compile the Java files: `javac *.java`
2. Run the application: `java Archive`
3. Enter commands interactively or pipe from a file

## Design Principles
- **Object-Oriented Design**: Proper encapsulation with separate classes for Student and SpellBook
- **Command Pattern**: Centralized command processing in Archive class
- **Data Integrity**: Proper tracking of rental history and current state
- **Error Handling**: Graceful handling of invalid inputs and edge cases