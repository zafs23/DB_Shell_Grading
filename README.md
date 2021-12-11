****************
* Project Name : Databases Final Project
* Class : CS 410, Fall 2021
* Date : 12/10/2021
* Team Members : Becka Seevers, Sajia Zafreen
**************** 



## OVERVIEW:
 
 This project implements a command shell application in Java for managing grades in a 
 class. 


## INCLUDED FILES:

 * GradeManager.java - source file
 * DBShell.java - source file
 * JDBCConnect.java - source file
 * README - this file
 * E-R_Model.pdf - design file
 * schema.sql - Database Script
 * dump.sql - Example Data in MySQL dump file


## COMPILING AND RUNNING:
 
 To run the shell command first compile using the following command:
```
 $ javac DBShell.java
 ```
 
 After that to run the compiled class to test the output type the following command:
 ```
 $ java DBShell
 ```
 
 ## PROGRAM DESIGN AND IMPORTANT CONCEPTS:
 
 The program design is implemented in the E-R model. Then the database schema is implemented using the schema.sql script. 

A while loop, scanner, and switch-case statement was used to implement the Java shell. A verbose usage statement and option to quit was implemented to improve the usability of the shell program. 

Once the program is start, the JDBC connection begins and then the connection is passed to GradeManager.java. User input is handled in DBShell to validate the input before it's used to create SQL statements in GradeManager. SQL exceptions are thrown in GradeManager and then handled in DBShell. Invalid input will allow the program to continue, but auto commit is set to false at the beginning of the program and rollbacks are in each SQL exception catch to SQL statements that aren't able to execute don't.
 
 ## TESTING:
 
 For testing, the DBShell program was run and mock data was entered through the program to ensure insertion and output was implemented correctly. Any errors that came up were either resolved by editing the queries in GradeManager, or for more complex queries, they were run in MySQL Workbench then added into GradeManager once they had the correct syntax. Java debugger was also used to step through parts of the program that were harder to implement.
 
 
## DISCUSSION:
 To implement the queries, the E-R model helped a lot. Some class notes are used as reference to implement complicated queries. It was also useful to implement the more complicated queries in MySQL Workbench before adding them in GradeManager. That way you could make changes to the queries faster than if you had to recompile the program every time in Java.
 

## Demo:
[Demo Video](https://drive.google.com/file/d/1ELrKrhPjZoM8blx79_XyCVvsacEzcu13/view?usp=sharing)