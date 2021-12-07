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
            "\tshow-categories lists the categories with their weights\n" +
            "\tadd-category adds a new category\n" +
            "\tshow-assignment lists the assignments with their point values, grouped by category\n" +
            "\tadd-assignment adds a new assignment\n" +
            "\tadd-student adds a student and enrolls them in the current class\n" +
            "\tshow-students shows all students in the current class\n" +
            "\tgrade-assignment assigns a grade to the assignment for the given student\n" +
            "\tstudent-grades shows the student's current grades\n" +
            "\tgradebook shows the current class's gradebook\n\n" +
            "Command input after class is selected:\n" +
            "\tshow-categories\n" +
            "\tadd-category name weight\n" +
            "\tshow-assignment\n" +
            "\tadd-assignment name category description points\n" +
            "\tadd-student username studentid last first\n" +
            "\tshow-students [string]\n" +
            "\tgrade-assignment name username grade\n" +
            "\tstudent-grades username\n" +
            "\tgradebook\n\n\n" +
            "Enter q to quit the program and h to see this usage statement again\n";

    public static void main(String[] args) {
        System.out.println("\n==============================\nWelcome to Grade Manager!"
            + "\n==============================\n\n");
        System.out.println("Please enter JDBC connection information:\nportnumber password\n");
        
        Scanner input = new Scanner(System.in);
        boolean success = false;
        Connection con = null;
        int port = 0;
        String pwd = "";

        while(!success) {
            String ln = input.nextLine();
            String conarr[] = ln.split(" ", 2);
            if (conarr.length < 2 || conarr.length > 2) {
                System.out.println("Connection requires 2 arguments: portnumber password\n");
            } else {
                port = Integer.parseInt(conarr[0]);
                pwd = conarr[1];
                // start connection
                try {
                    con = JDBCConnect.establishConnection(port, pwd);
                    if (con != null) {
                        success = true;
                    }
                } catch (ClassNotFoundException e) {
                    System.out.println(e.getMessage());

                }
            }
        }
        System.out.println("Connection successful!\n\n");

        System.out.println("Enter a command to begin grade management. Enter q to quit or h for help.\n\n");

        // input is closed either in while loop when program is quit or in catch
        input = new Scanner(System.in);

        // set up GradeManager
        GradeManager mn = new GradeManager(con);

        try {
            // while loop to keep shell going unless user exits
            while(true) {
                String cmdLine = input.nextLine();
                String arr[] = cmdLine.split(" ",2);
                String cmd = arr[0];
                String cmdArgs = "";
                if (arr.length > 1){
                    cmdArgs = arr[1];
                }

                // TODO: in each case statement, call the GradeManager function
                switch (cmd) {
                    case "new-class":
                        System.out.println(cmdArgs);
                        break;
                    case "list-classes":
                        System.out.println("Classes:\n");
                        break;
                    case "select-class":
                        System.out.println(cmdArgs);
                        break;
                    case "show-class":
                        System.out.println(cmdArgs);
                        break;
                    case "show-categories":
                        System.out.println(cmdArgs);
                        break;
                    case "add-category":
                        System.out.println(cmdArgs);
                        break;
                    case "show-assignment":
                        System.out.println(cmdArgs);
                        break;
                    case "add-assignment":
                        System.out.println(cmdArgs);
                        break;
                    case "add-student":
                        System.out.println(cmdArgs);
                        break;
                    case "show-students":
                        System.out.println(cmdArgs);
                        break;
                    case "grade-assignment":
                        System.out.println(cmdArgs);
                        break;
                    case "student-grades":
                        System.out.println(cmdArgs);
                        break;
                    case "gradebook":
                        System.out.println("Grades:\n");
                        break;
                    case "q":
                        System.out.println("Quitting Grade Manager");
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


