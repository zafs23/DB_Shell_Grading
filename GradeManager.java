
import java.sql.*;
import java.util.*;

/**
 * GradeManager is instantiated with a JDBC connection and is reponsible for
 * running SQL queries. Implemented in DBShell.java
 */
public class GradeManager {
	private final Connection db;
	private int currActiveClass;
	private String activateClassQuery = "SELECT class_id, class_year, class_term FROM class where class_num = ?"; //use only when these selects needed

	public GradeManager(Connection con) {
		this.db = con;
		this.currActiveClass = 0;
	}

	// Create a new 'class' for the database table
	public void newClass(String classNum, String termYear, int sectionNum, String description) throws SQLException {
		List<Object> tmp = parseTermYear(termYear);
		if (tmp == null) {
			System.out.println("ERROR: term couldn't be parsed - expecting 2 letters and 2 digits, i.e. Sp20");
			return;
		}
		String term = (String) tmp.get(0);
		int year = (int) tmp.get(1);

		String createNewClassQuery = "INSERT INTO class (class_num, class_term, class_year, class_sec_num, class_description) "
				+ "VALUES(?, ?, ?, ?, ?)";
		insertQuery(createNewClassQuery, classNum, term, year, sectionNum, description);
		System.out.println("Class inserted!");
	}

	private void insertQuery(String query, Object... values) throws SQLException {
		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeManager.insertValues(stmt, values);
			stmt.execute();
			this.db.commit();
		}

	}

	private static void insertValues(PreparedStatement stmt, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			stmt.setObject(i + 1, values[i]); // remember, here that the id/primary key starts from 1, thus
												// currActiveClass = 0, distinguishes the initial state
		}

	}

	private static void displayColumnHeaders(ResultSetMetaData rsmd) throws SQLException {
		String output = "";
		int colCt = rsmd.getColumnCount();
		for (int i = 1; i <= colCt; i++) {
			output += rsmd.getColumnName(i);
			if (i != colCt) {
				output += " | ";
			}
		}
		System.out.println(output);
	}

	// list classes with number of students in it
	public void listClasses() throws SQLException {
		String queryListClasses = "SELECT class_num, class_sec_num, class_term, class_year, COUNT(student_id) AS num_students FROM class LEFT JOIN enroll ON "
				+ "class.class_id = enroll.class_id GROUP BY enroll.class_id, class_sec_num, class_year, class_term, class_num ORDER BY class_num, class_sec_num";
		try (PreparedStatement stmt = db.prepareStatement(queryListClasses)) {
			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				System.out.println("List of classes:\n");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getString("class_num") + "\t | \t" + rs.getInt("class_sec_num") + "\t | \t" 
						+ rs.getString("class_term") + "\t | \t" + rs.getInt("class_year") + "\t | \t" + rs.getString("num_students"));
				}
				System.out.println();
			}
		}
	}

	// Activate a class - select class class_number
	public void selectClass(String classNumber) throws SQLException {
		String querySelectClass = activateClassQuery + "ORDER BY class_year DESC, class_term";
		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber);
			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					int oldClass = this.currActiveClass;
					this.currActiveClass = rs.getInt("class_id"); // update the active class id
					// if there is multiple sections this fails
					if (rs.next()) {
						if ((rs.getInt("class_year") == rs.getInt("class_year"))
								&& (rs.getString("class_term").equals(rs.getString("class_term")))) {
							System.out.println("Error! Multiple classes for class number exits. ");
							this.currActiveClass = oldClass;
						}
					} else {
						System.out.println("Class '" + classNumber + "' is selected successfully.\n");
					}
				} else {
					System.out.println("Class does not exist");
				}
			}
		}

	}

	// Activate a class - select class class_number term
	public void selectClass(String classNumber, String termYear) throws SQLException {
		List<Object> tmp = parseTermYear(termYear);
		if (tmp == null) {
			System.out.println("ERROR: term couldn't be parsed - expecting 2 letters and 2 digits, i.e. Sp20");
			return;
		}
		String term = (String) tmp.get(0);
		int year = (int) tmp.get(1);

		String querySelectClass = activateClassQuery + "AND class_term = ? AND class_year = ?";

		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber, term, year);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					if (!rs.next()) {
						this.currActiveClass = rs.getInt("class_id");
						System.out.println("Class '" + classNumber + "' is selected successfully.\n");
					} else {
						System.out.println("Error! Multiple classes for class number exists. ");
					}
				} else {
					System.out.println("Class does not exist");
				}
			}
		}
	}

	// Activate a class - select class class_number term section
	public void selectClass(String classNumber, String termYear, int section) throws SQLException {
		List<Object> tmp = parseTermYear(termYear);
		if (tmp == null) {
			System.out.println("ERROR: term couldn't be parsed - expecting 2 letters and 2 digits, i.e. Sp20");
			return;
		}
		String term = (String) tmp.get(0);
		int year = (int) tmp.get(1);

		String querySelectClass = "SELECT class_id, class_sec_num, class_year, class_term FROM class where class_num = ? AND class_term = ? AND class_year = ?"
				+ " AND class_sec_num = ?";

		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber, term, year, section);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					this.currActiveClass = rs.getInt("class_id");
					System.out.println("Class '" + classNumber + "' is selected successfully.\n");

				} else {
					System.out.println("Class does not exist");
				}
			}
		}
	}

	// Show the currently active class : show-class
	public void showClass() throws SQLException {
		String queryShowClass = "SELECT class_num, class_term, class_year, class_sec_num, class_description FROM class WHERE class_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(queryShowClass)) {
			GradeManager.insertValues(stmt, this.currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				System.out.println("The currectly active class: \n");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getString("class_num") + "\t | \t" + rs.getString("class_term") + "\t | \t"
							+ rs.getInt("class_year") + "\t | \t" + rs.getInt("class_sec_num") + "\t | \t"
							+ rs.getString("class_description") + "\n");
				}
			}
		}
	}

	// helper function to separate term and year for queries
	public List<Object> parseTermYear(String termYear) {
		String term = "";
		String tmpyear = "";
		int ct = 0;
		List<String> termOpts = Arrays.asList("Sp", "Fa", "Su");
		for (int i = 0; i < termYear.length(); i++) {
			if (!Character.isDigit(termYear.charAt(i))) {
				// handling if term isn't given with first letter in uppercase
				if (i == 0 && !Character.isUpperCase(termYear.charAt(i))) {
					Character.toUpperCase(termYear.charAt(i));
				} else {
					term = term + termYear.charAt(i);
				}
			}
			if (Character.isDigit(termYear.charAt(i))) {
				tmpyear = tmpyear + termYear.charAt(i);
				ct++;
			}
		}
		int year = Integer.parseInt(tmpyear);
		if (!termOpts.contains(term)) {
			System.out.println("ERROR: term doesn't match available term options ('Sp', 'Fa', 'Su')");
			return null;
		}
		if (ct < 2 || ct > 2) {
			System.out.println("ERROR: term year must be only 2 digits long - i.e. Fall 2019 would be Fa19");
			return null;
		}
		return Arrays.asList(term, year);
	}

	// Category and Assignment Management

	// show-categories
	public void showCategories() throws SQLException {
		String showCategoryQuery = "SELECT category_name, category_weight FROM category " + "JOIN class ON category.class_id = class.class_id "
				+ "WHERE class.class_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(showCategoryQuery)) {
			GradeManager.insertValues(stmt, this.currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				System.out.println("Categories for the active class:\n");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getString("category_name") + "\t |\t" + rs.getInt("category_weight"));
				}
			}
		}
	}

	// add a new category
	public void addCategory(String name, int weight) throws SQLException {
		if (currActiveClass == 0) {
			System.out.println("Error! No active class is selected.");
			return;
		}
		String queryAddCategory = "INSERT INTO category (category_name, category_weight, class_id) "
				+ "VALUES (?, ?, ?)";
		insertQuery(queryAddCategory, name, weight, this.currActiveClass);
		System.out.println("Category " + name + " has been added.");
	}

	// List all assignments with point values, grouped by category
	public void showAssignments() throws SQLException {
		String query = "SELECT assignments.assignments_name, assignments.assignments_point_value " + "FROM assignments "
				+ "INNER JOIN category ON assignments.category_id = category.category_id "
				+ "INNER JOIN class ON class.class_id = category.class_id " + "WHERE category.class_id = ? "
				+ "GROUP BY category.category_id, assignments.assignments_name, assignments_point_value";

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeManager.insertValues(stmt, this.currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				System.out.println("Assignment Lists: \n");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out
							.println(rs.getString("assignments_name") + "\t | \t" + rs.getInt("assignments_point_value"));
				}
			}
		}
	}

	// add a new assignment
	public void addNewAssignment(String name, String category_name, String description, int point) throws SQLException {
		if (this.currActiveClass == 0) {
			System.out.println("Error! No active class is selected.");
			return;
		}

		// first check the category ID for the assignment
		String queryCategory_ID = "SELECT category_id FROM category WHERE category_name = ? AND class_id = ?";

		int category_id = 0;
		try (PreparedStatement stmt = db.prepareStatement(queryCategory_ID)) {
			GradeManager.insertValues(stmt, category_name, this.currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					category_id = rs.getInt("category_id");
				}
			}
		}

		if (category_id == 0) {
			System.out.println("There is no category similar to " + category_name + "\n");
			return;
		}

		// if category exists, do the actual query
		String queryAddAssignment = "INSERT INTO assignments (assignments_name, assignments_description, assignments_point_value, category_id) "
				+ "VALUES (?, ?, ?, ?)";
		insertQuery(queryAddAssignment, name, description, point, category_id);

		System.out.println("Assignment " + name + " is added.\n");
	}

	// check if student exists, check with username, as it has to be unique
	private int studentIDCurrClass(String username) throws SQLException {
		int student = 0;
		String queryStudent_ID = "SELECT student_id FROM students WHERE student_username = ? ";

		try (PreparedStatement stmt = db.prepareStatement(queryStudent_ID)) {
			GradeManager.insertValues(stmt, username);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					student = rs.getInt("student_id"); // returns a 0 if the value doesn't exists
				}
			}
		}

		return student;
	}

	// check if assignment exits
	private int assignemntIDCurrClass(String assignmentName) throws SQLException {
		int assignment = 0;
		String queryStudent_ID = "SELECT assignments_id FROM assignments WHERE assignments_name = ? ";

		try (PreparedStatement stmt = db.prepareStatement(queryStudent_ID)) {
			GradeManager.insertValues(stmt, assignmentName);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					assignment = rs.getInt("assignments_id"); // returns a 0 if the value doesn't exists
				}
			}
		}

		return assignment;
	}

	// Student Management
	// add a student to the student table and enroll to a class
	public void addStudent(String username, int student_id, String nameLast, String nameFirst) throws SQLException {
		String student_name = nameFirst + " " + nameLast;

		// have to check if the student ID already exists
		int student = studentIDCurrClass(username);

		// student doesn't exist, add the studnt in student table
		if (student == 0) {
			String queryAddStudent = "INSERT INTO students (student_id, student_username, student_name) "
					+ "VALUES (?, ?, ?)";
			insertQuery(queryAddStudent, student_id, username, student_name);

		} else {
			// student exists
			// Now update the student name if doesn't match with database
			String queryStudent_Name = "SELECT student_name FROM students WHERE student_id = ? ";

			String studentName = "";
			try (PreparedStatement stmt = db.prepareStatement(queryStudent_Name)) {
				GradeManager.insertValues(stmt, student_id);

				try (ResultSet rs = stmt.executeQuery()) {
					this.db.commit();
					if (rs.next()) {
						studentName = rs.getString("student_name");
					}
				}
			}

			if (!studentName.equals(student_name)) {
				// do the update query here
				String queryUpdateStudent = "UPDATE students SET student_username = ?  WHERE student_id = ? ";

				try (PreparedStatement update = db.prepareStatement(queryUpdateStudent)) {
					GradeManager.insertValues(update, student_name, student_id);
					update.executeUpdate();
					this.db.commit();
					System.out.println("Student already exists in a different name. The student name is updated. \n");
					return;
				}
			}
		}

		// enroll the student as well
		String queryEnrollStudent = "INSERT INTO enroll (student_id, class_id)  VALUES (?, ?)";
		insertQuery(queryEnrollStudent, student_id, this.currActiveClass);
		System.out.println(student_name + " was added to class");
	}

	// add-student username
	public void addStudentUsername(String username) throws SQLException {

		// have to check if the student ID already exists
		String queryStudent_ID = "SELECT student_id FROM students WHERE (student_username = ?) ";

		int student_id = 0;
		try (PreparedStatement stmt = db.prepareStatement(queryStudent_ID)) {
			GradeManager.insertValues(stmt, username);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					student_id = rs.getInt("student_id");
				}
			}
		}

		if (student_id != 0) {
			// enroll the student
			String queryEnrollStudent = "INSERT INTO enroll (student_id, class_id)  VALUES (?, ?)";
			insertQuery(queryEnrollStudent, student_id, this.currActiveClass);

		} else {
			System.out.println("Error! Student doesn't exist. \n");
			return;
		}
	}

	// show all students
	public void showStudents(String match) throws SQLException {
		String query = "SELECT s.student_id, s.student_username, s.student_name FROM students AS s "
				+ "INNER JOIN enroll ON s.student_id = enroll.student_id WHERE enroll.class_id = ?";
		if (match != "") {
			// prepare statement has issues with % so have to do concat like below
			query += " AND s.student_name LIKE CONCAT( '%',?,'%') OR s.student_username LIKE CONCAT( '%',?,'%')";
		}

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			if (match == "") {
				GradeManager.insertValues(stmt, this.currActiveClass);
			} else {
				GradeManager.insertValues(stmt, this.currActiveClass, match, match);
			}

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				System.out.println("Students from the current class:\n");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getInt("student_id") + "\t | \t" + rs.getString("student_username") + "\t | \t"
							+ rs.getString("student_name"));
				}
			}
		}
	}

	// grade the given assignment for the passed in student
	// TODO: get number of rows inserted as confirmation (we should do this for all
	// inserts) - done with an helping function
	// TODO: I think this query will throw error if not found add in handle for
	// assignment or student not found
	// but if it doesn't, I'll add in separate queries to check that the assignment
	// and student exist
	public void grade(String assignName, String username, int grade) throws SQLException {

		// check if student exits
		int student = 0;
		student = studentIDCurrClass(username);
		if (student == 0) {
			System.out.println("Error! The student doesn't exist!\n");
			return;
		}

		// check if the assignment exits
		int assignment_id = 0;
		assignment_id = assignemntIDCurrClass(assignName);
		if (assignment_id == 0) {
			System.out.println("Error! The assignment doesn't exist!\n");
			return;
		}

		// check if grade is greater than the point value allocated for the assignment
		int assignment_value = 0;
		String queryAssignPoint = "SELECT assignments_point_value FROM assignments WHERE assignments_id = ? ";

		try (PreparedStatement stmt = db.prepareStatement(queryAssignPoint)) {
			GradeManager.insertValues(stmt, assignment_id);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) {
					assignment_value = rs.getInt("assignments_point_value"); // returns a 0 if the value doesn't exists
				}
			}
		}

		if (assignment_value < grade) {
			// update/input but with a warning. Cases like extra credits
			System.out.println(
					"Warning! The highest point configured for this assignment is " + assignment_value + ".\n");
		}

		// then check if only update is needed
		String queryGrade = "Select grades_score FROM grades WHERE student_id = ? AND assignments_id = ?";
		try (PreparedStatement stmt = db.prepareStatement(queryGrade)) {
			GradeManager.insertValues(stmt, student, assignment_id);

			try (ResultSet rs = stmt.executeQuery()) {
				this.db.commit();
				if (rs.next()) { // row exists
					String queryUpdateGrade = "UPDATE grades SET grades_score = ? WHERE student_id = ? AND assignments_id = ?";

					try (PreparedStatement update = db.prepareStatement(queryUpdateGrade)) {
						GradeManager.insertValues(update, grade, student, assignment_id);
						update.executeUpdate();
						this.db.commit();
						System.out.println("Grade updated. To change the grade run the command with new grade again\n");
						return;
					}
				}
			}
		}

		//Now input the value
		String queryInputGrade = "INSERT INTO grades (grades_score, student_id, assignments_id) VALUES(?, ?, ?)";
		insertQuery(queryInputGrade, grade, student, assignment_id);
		System.out.println("Grade updated. To change the grade run the command with new grade again\n");
		
	}
	

	// show student's current grade - assignments grouped by category with grade and subtotals for each category
	// and overall grade for class
	public void studentGrades(String username) throws SQLException {
		// check if student exits
		int student = 0;
		student = studentIDCurrClass(username);
		if (student == 0) {
			System.out.println("Error! The student doesn't exist!\n");
			return;
		}



		// get assignment name and score, grouped by category
		String getGradesQuery = "SELECT c.category_name, a.assignments_name, (CAST(g.grades_score AS float) / CAST(a.assignments_point_value AS float)) * 100.0 AS grade FROM grades AS g JOIN assignments AS a " +
			"ON g.assignments_id = a.assignments_id JOIN category as c ON a.category_id = c.category_id WHERE g.student_id = ? AND c.class_id = ? GROUP BY " + 
			"c.category_name, a.assignments_name, grade, g.grades_score, a.assignments_point_value ORDER BY c.category_name, a.assignments_name";

		// get category subtotal
		String getCategorySubtotal = "SELECT c.category_name, (CAST(SUM(g.grades_score) AS float) / CAST(SUM(a.assignments_point_value) AS float)) * 100.0 AS category_grade FROM grades AS g " +
			"JOIN assignments AS a ON g.assignments_id = a.assignments_id JOIN category as c ON a.category_id = c.category_id WHERE g.student_id = ? AND c.class_id = ? GROUP BY c.category_name ORDER BY c.category_name";

		// get total grade
		String getTotalGrade = "SELECT SUM(CAST((CAST(x.category_weight AS float) / 100.0) * x.category_grade AS float)) AS total_grade " + 
			"FROM (SELECT c.category_name, c.category_weight, (CAST(SUM(g.grades_score) AS float) / CAST(SUM(a.assignments_point_value) AS float)) * 100.0 AS category_grade FROM grades AS g " +
			"JOIN assignments AS a ON g.assignments_id = a.assignments_id JOIN category as c ON a.category_id = c.category_id " +
			"WHERE g.student_id = ? AND c.class_id = ? GROUP BY c.category_name ORDER BY c.category_name) x";

		// execute get grades query
		try (PreparedStatement stmt = db.prepareStatement(getGradesQuery)) {
			GradeManager.insertValues(stmt, student, this.currActiveClass);
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Grades for student: " + username);
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getString("category_name") + "\t | \t " + rs.getString("assignments_name") + "\t | \t" + rs.getInt("grade"));
				}
			}
		}
		

		// execute get category subtotal query
		try (PreparedStatement stmt = db.prepareStatement(getCategorySubtotal)) {
			GradeManager.insertValues(stmt, student, this.currActiveClass);
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Category subtotals:");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getString("category_name") + "\t | \t" + rs.getFloat("category_grade"));
				}
			}
		}

		// execute get total grade query
		try (PreparedStatement stmt = db.prepareStatement(getTotalGrade)){
			GradeManager.insertValues(stmt, student, this.currActiveClass);
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Total grade: " + rs.getFloat("total_grade"));
			}
		}
	}

	// show's entire classes gradebook, including student info and total grades in class
	public void gradebook() throws SQLException {
		String query = "SELECT x.student_id, x.student_username, x.student_name, SUM(x.points_earned) AS points_earned, SUM(x.total_points) AS total_points, SUM(CAST((CAST(x.category_weight AS float) / 100.0) * x.category_grade AS float)) AS total_grade " +
		"FROM " +
			"(SELECT s.student_id, s.student_username, s.student_name, c.category_name, c.category_weight, SUM(g.grades_score) AS points_earned, SUM(a.assignments_point_value) AS total_points, (CAST(SUM(g.grades_score) AS float) / CAST(SUM(a.assignments_point_value) AS float)) * 100.0 AS category_grade FROM grades AS g " +
			"JOIN assignments AS a ON g.assignments_id = a.assignments_id " +
			"JOIN category as c ON a.category_id = c.category_id " +
			"JOIN students AS s ON g.student_id = s.student_id " +
			"WHERE c.class_id = ? " +
			"GROUP BY c.category_name, s.student_id, s.student_username, s.student_name " +
			"ORDER BY c.category_name) x " +
		"GROUP BY x.student_id, x.student_username, x.student_name "; 

		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeManager.insertValues(stmt, this.currActiveClass);
			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Grades for all students in class:");
				ResultSetMetaData rsmd = rs.getMetaData();
				GradeManager.displayColumnHeaders(rsmd);
				while (rs.next()) {
					System.out.println(rs.getInt("student_id") + "\t|\t" + rs.getString("student_username") + "\t|\t" + rs.getString("student_name") + "\t|\t" + rs.getInt("points_earned") + 
						"\t|\t" + rs.getInt("total_points") + "\t|\t" + rs.getFloat("total_grade"));
				}
			}
		}
	}
	
}
