# data-structures-avl-stock-manager
# AVL Stock Manager

##  Overview
This project implements a stock management system using an AVL Tree as part of a Data Structures course.

The system efficiently manages stock price updates over time, supporting fast queries, insertions, and deletions while maintaining a balanced tree structure.

## Core Concepts
- AVL Tree (self-balancing binary search tree)
- Logarithmic time complexity operations
- Time-based data management (timestamps)
- Efficient range queries

##  Features
- Initialize stock system
- Add new stocks with initial price
- Remove stocks
- Update stock prices over time (with timestamps)
- Retrieve current stock price
- Remove specific historical price updates
- Count number of stocks within a price range
- Retrieve stock IDs within a price range

## Time Complexity
- Insert / Remove / Update: **O(log N)**
- Range queries: **O(log N + K)**
- Search operations: **O(log N)**

##  Architecture
The system is built around:
- `AVLTree.java` – handles balanced tree operations
- `StockManager.java` – manages stock logic and operations

Each stock is identified by a unique `stockId` and maintains a history of price updates.

## Input Validation
- Prevents duplicate stock insertion
- Validates price updates
- Ensures proper handling of edge cases (e.g., empty structures)

##  Academic Context
Developed as part of a university Data Structures course.  
Final grade: **100**

##  Technologies
- Java
- Object-Oriented Programming
- Data Structures

##  How to Run
1. Open the project in IntelliJ / Eclipse / VS Code
2. Compile the Java files
3. Run `Main.java`

##  Notes
This project demonstrates strong understanding of advanced data structures and algorithmic efficiency.
