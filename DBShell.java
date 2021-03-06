import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Driver class for CS410 Final Project. To compile and run:
 * javac DBShell.java
 * java DBShell
 * 
 * Usage will come up to guide you through shell program.
 * q to exit (or Ctrl+c) and h for help
 */
public class DBShell {

    // usage stmt constant
    final static String usage = "USAGE:\n\tArguments not in braces are required. \n\tClass name is 1 word with department in caps (i.e. CS410).\n" +
            "\tTerm is in format semesterYear (i.e. Sp20, Fa19, Su20)\n" +
            "\tnew-class will create a class\n" +
            "\tlist-classes will list classes with the number of students in each class\n" +
            "\tselect-class will activate specified class for future commands, such as adding students to that class\n" +
            "\tshow-class will show which class is currently activated\n\n" +
            "Command input:\n" +
            "\tnew-class class-name term section \"description\"\n" +
            "\tlist-classes\n" +
            "\tselect-class class-name [term] [section]\n" +
            "\tshow-class\n\n\n" +
            "The following commands can only be run if a class has been selected:\n" +
            "\tshow-categories lists the categories with their weights. Weights are shown as 0 to 100\n" +
            "\tadd-category adds a new category with weight represented as a value between 0 and 100\n" +
            "\tshow-assignment lists the assignments with their point values, grouped by category\n" +
            "\tadd-assignment adds a new assignment\n" +
            "\tadd-student adds a student and enrolls them in the current class\n" +
            "\tshow-students shows all students in the current class\n" +
            "\tgrade assigns a grade to the assignment for the given student\n" +
            "\tstudent-grades shows the student's current grades\n" +
            "\tgradebook shows the current class's gradebook\n\n" +
            "Command input after class is selected:\n" +
            "\tshow-categories\n" +
            "\tadd-category name weight\n" +
            "\tshow-assignment\n" +
            "\tadd-assignment name category description points\n" +
            "\tadd-student username [studentid] [last] [first]\n" +
            "\tshow-students [string]\n" +
            "\tgrade assignmentname username grade\n" +
            "\tstudent-grades username\n" +
            "\tgradebook\n\n\n" +
            "Enter q to quit the program and h to see this usage statement again\n";

    public static void main(String[] args) {
        System.out.println("\n==============================\nWelcome to Grade Manager!"
            + "\n==============================\n\n");
        System.out.println("Connecting to database.......\n");
        
        Scanner input = new Scanner(System.in);
        Connection con = null;

        // set up connection
        try {
            con = JDBCConnect.establishConnection();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        if (con == null) {
            System.out.println("There was an issue connecting to the database. Exiting program.\n");
            input.close();
            System.exit(1);
        }
        
        System.out.println("\nConnection successful!\n\n");

        System.out.println("Enter a command to begin grade management. Enter q to quit or h for help.\n\n");

        try {
            // set up GradeManager
            con.setAutoCommit(false);
            GradeManager mn = new GradeManager(con);
            
            // while loop to keep shell going unless user exits
            while(true) {
                String cmdLine = input.nextLine();
                String arr[] = cmdLine.split(" ",2);
                String cmd = arr[0];
                String cmdArgs = "";
                boolean valid = false;
                if (arr.length > 1){
                    cmdArgs = arr[1];
                }

                // switch case to handle all input
                switch (cmd) {
                    case "new-class":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 4);
                            if (opts.length == 4) {
                                String classNum = opts[0];
                                String termYear = opts[1];
                                int sectionNum = Integer.parseInt(opts[2]);
                                String description = opts[3];
                                if (description.charAt(0) != '"') {
                                    description = "\"" + description;
                                }
                                if (description.charAt(description.length()-1) != '"') {
                                    description += "\"";
                                }
                                valid = true; // no errs with args
                                try {
                                    mn.newClass(classNum, termYear, sectionNum, description);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting new-class class-name term section \"description\"");
                        }
                        
                        break;

                    case "list-classes":
                        if (cmdArgs == "") {
                            valid = true;
                            try {
                                mn.listClasses();
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting list-classes");
                        }
                        break;

                    case "select-class":
                        if (cmdArgs != "") {
                            String classopts[] = cmdArgs.split(" ", 3);
                            if (classopts.length >= 1 && classopts.length <= 3) {
                                valid = true;
                                String classNum = classopts[0];
                                String classTerm = "";
                                int section = -1;
                                if (classopts.length > 1) {
                                    classTerm = classopts[1];
                                    if (classopts.length > 2) {
                                        section = Integer.parseInt(classopts[2]);
                                    }
                                }
                                try {
                                    if (classopts.length == 1) {
                                        mn.selectClass(classNum);
                                    } else if (classopts.length == 2) {
                                        mn.selectClass(classNum, classTerm);
                                    } else {
                                        mn.selectClass(classNum, classTerm, section);
                                    }
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                                
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting select-class class-name [term] [section]");
                        }
                        break;

                    case "show-class":
                        if (cmdArgs == "") {
                            valid = true;
                            try {
                                mn.showClass();
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting show-class");
                        }
                        break;

                    case "show-categories":
                        if (cmdArgs == "") {
                            valid = true;
                            try {
                                mn.showCategories();
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }
                        
                        if (!valid) {
                            System.out.println("Invalid input. Expecting show-categories");
                        }
                        break;

                    case "add-category":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 2);
                            if (opts.length > 0 && opts.length <= 2) {
                                String name = opts[0];
                                int weight = Integer.parseInt(opts[1]);
                                if (weight >= 0 && weight <= 100) {
                                    valid = true;
                                    try {
                                        mn.addCategory(name, weight);
                                    } catch (SQLException e) {
                                        con.rollback();
                                        System.out.println(e.getMessage());
                                    }
                                }
                                
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting add-category name weight\nWhere weight is from 0 to 100");
                        }
                        break;
                        
                    case "show-assignment":
                        if (cmdArgs == "") {
                            valid = true;
                            try {
                                mn.showAssignments();
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }
                        if (!valid) {
                            System.out.println("Invalid input. Expecting show-assignment");
                        }
                        break;
                    case "add-assignment":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 3);
                            int points = -1;
                            points = Integer.parseInt(opts[2].substring(opts[2].lastIndexOf(" ") + 1));
                            String description = opts[2].substring(0, opts[2].lastIndexOf(" "));
                            if (opts.length == 3 && points != -1) {
                                String name = opts[0];
                                String category = opts[1];
                                valid = true;
                                try {
                                    mn.addNewAssignment(name, category, description, points);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            }
                        }

                        if (!valid) {
                            System.out.println("Invalid input. Expecting add-assignment name category description points");
                        }
                        break;

                    case "add-student":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 4);
                            if (opts.length == 4) {
                                String username = opts[0];
                                int studentid = Integer.parseInt(opts[1]);
                                String last = opts[2];
                                String first = opts[3];
                                valid = true;
                                try {
                                    mn.addStudent(username, studentid, last, first);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            } else if (opts.length == 1) {
                                String username = opts[0];
                                valid = true;
                                try {
                                    mn.addStudentUsername(username);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        if (!valid) {
                            System.out.println("Invalid input. Expecting add-student username [studentid] [last] [first]");
                        }
                        break;

                    case "show-students":
                        String match = "";
                        if (cmdArgs == "") {
                            valid = true;
                            match = "";
                        } else {
                            String opts[] = cmdArgs.split(" ", 1);
                            match = opts[0];
                            valid = true;
                        }
                        if (valid) {
                            try {
                                mn.showStudents(match);
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }
                        if (!valid) {
                            System.out.println("Invalid input. Expecting show-students [string]");
                        }
                        break;

                    case "grade":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 3);
                            if (opts.length == 3) {
                                valid = true;
                                String assignName = opts[0];
                                String username = opts[1];
                                int grade = Integer.parseInt(opts[2]);
                                try {
                                    mn.grade(assignName, username, grade);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        if (!valid) {
                            System.out.println("Invalid input. Expecting grade assignmentname username grade");
                        }
                        break;

                    case "student-grades":
                        if (cmdArgs != "") {
                            String opts[] = cmdArgs.split(" ", 1);
                            if (opts.length == 1) {
                                valid = true;
                                try {
                                    mn.studentGrades(opts[0]);
                                } catch (SQLException e) {
                                    con.rollback();
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        
                        if (!valid) {
                            System.out.println("Invalid input. Expecting student-grades username");
                        }
                        break;

                    case "gradebook":
                        if (cmdArgs == "") {
                            valid = true;
                            try {
                                mn.gradebook();
                            } catch (SQLException e) {
                                con.rollback();
                                System.out.println(e.getMessage());
                            }
                        }
                        if (!valid) {
                            System.out.println("Invalid input. Expecting student-grades username");
                        }
                        break;

                    case "q":
                        System.out.println("Quitting Grade Manager");
                        try { // nested trys aren't ideal but need to catch SQL exception for rollback attempt
                            con.rollback();
                        } catch (SQLException err) {} // do nothing if con doesn't need to be rolled back
                        input.close();
                        System.exit(0);
                    case "h":
                        System.out.println(usage);
                        break;
                    default:
                        System.out.println("Command not recognized. Enter h for help or q to quit.\n");
                }
            }
        } catch (Exception e) { // catch general exception to ensure scanner gets closed
            try { // nested trys aren't ideal but need to catch SQL exception for rollback attempt
                con.rollback();
            } catch (SQLException err) {
                System.out.println(err.getMessage());
            }
            
            System.out.println(e.getMessage());
            input.close();
        }
    }
}


