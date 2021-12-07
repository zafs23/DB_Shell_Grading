
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * GradeManager is instantiated with a JDBC connection and is reponsible
 * for running SQL queries. Implemented in DBShell.java
 */
public class GradeManager {
	private final Connection db;
	private int currActiveClass;
	private String activateClassQuery = "SELECT class_id, class_year, class_term FROM class where class_num = ? ";

	public GradeManager(Connection con) {
		this.db = con;
		this.currActiveClass = 0;
	}

	// Create a new 'class' for the database table
	public void newClass(String classNum, String term, int year, int sectionNum, String description)
			throws SQLException {
		String createNewClassQuery = "INSERT INTO class (class_num, class_term, class_year, class_sec_num, class_description) "
				+ "VALUES(?, ?, ?, ?, ?)";
		insertQuery(createNewClassQuery, classNum, term, year, sectionNum, description);
	}

	private void insertQuery(String query, Object... values) throws SQLException {
		try (PreparedStatement stmt = db.prepareStatement(query)) {
			GradeManager.insertValues(stmt, values);
			stmt.execute();
		}

	}

	private static void insertValues(PreparedStatement stmt, Object... values) throws SQLException {
		for (int i = 0; i < values.length; i++) {
			stmt.setObject(i + 1, values[i]); // remember, here that the id/primary key starts from 1, thus
												// currActiveClass = 0, distinguishes the initial state
		}

	}

	// list class with number of students in it, needs a join statement

	// Activate a class - select class class_number
	public void selectClass(String classNumber) throws SQLException {
		String querySelectClass = activateClassQuery + "ORDER BY class_year DESC, class_term";
		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					this.currActiveClass = rs.getInt("class_id"); // update the active class id
					// if there is multiple sections this fails
					if (rs.next()) {
						if ((rs.getInt("class_year") == rs.getInt("class_year"))
								&& (rs.getString("class_term").equals(rs.getString("class_term")))) {
							System.out.println("Error! Multiple classes for class number exits. ");
						}
					}

					System.out.println("Class '" + classNumber + "' is selected successfully.\n");
				}
			}
		}

	}

	// Activate a class - select class class_number term
	public void selectClass(String classNumber, String termYear) throws SQLException {
		List<Object> tmp = parseTermYear(termYear);
		String term = (String) tmp.get(0);
		int year = (int) tmp.get(1);
		
		String querySelectClass = activateClassQuery + "AND class_term = " + term + " AND class_year = " + year;

		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber, term, year);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					if (!rs.next()) {
						this.currActiveClass = rs.getInt("class_id");
						System.out.println("Class '" + classNumber + "' is selected successfully.\n");
					} else {
						System.out.println("Error! Multiple classes for class number exits. ");
					}
				}
			}
		}
	}

	// Activate a class - select class class_number term section
	public void selectClass(String classNumber, String termYear, int section) throws SQLException {
		List<Object> tmp = parseTermYear(termYear);
		String term = (String) tmp.get(0);
		int year = (int) tmp.get(1);

		String querySelectClass = activateClassQuery + "AND class_term = " + term + " AND class_year = " + year + "AND class_sec_num = " + section;

		try (PreparedStatement stmt = db.prepareCall(querySelectClass)) {
			GradeManager.insertValues(stmt, classNumber, term, year, section);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					this.currActiveClass = rs.getInt("class_id");
					System.out.println("Class '" + classNumber + "' is selected successfully.\n");

				}
			}
		}
	}

	// Show the currently active class : show-class
	public void showClass() throws SQLException {
		String queryShowClass = "SELECT * FROM class WHERE class_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(queryShowClass)) {
			GradeManager.insertValues(stmt, this.currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("The currectly Active class: \n");

				while (rs.next()) {
					System.out.println(rs.getString("class_num") + "\t |" + rs.getString("class_term") + "\t |"
							+ rs.getInt("class_year") + "\t |" + rs.getInt("class_sec_num") + "\t |"
							+ rs.getString("class_description") + "\n");
				}
			}
		}
	}

	public List<Object> parseTermYear(String termYear) {
		String term = "";
		String tmpyear = "";
		List<String> termOpts = Arrays.asList("Sp", "Fa", "Su");
		for (int i = 0; i < termYear.length(); i++){
			if (!Character.isDigit(termYear.charAt(i))) {
				// handling if term isn't given with first letter in uppercase
				if (i == 0 && !Character.isUpperCase(termYear.charAt(i))) {
					Character.toUpperCase(termYear.charAt(i));
				}
				term = term + termYear.charAt(i);
            }
			if (Character.isDigit(termYear.charAt(i))){
				tmpyear = tmpyear + termYear.charAt(i);
			}
		}
		int year = Integer.parseInt(tmpyear);
		if (!termOpts.contains(term)) {
			System.out.println("ERROR: term doesn't match available term options ('Sp', 'Fa', 'Su')");
			return null;
		}
		return Arrays.asList(term, year);
		
		
	}

		// Category and Assignment Management
	
	// show-categories
	public void showCategories() throws SQLException {
		String showCategoryQuery = "SELECT * FROM category " 
					 + "JOIN class ON category.class_id = class.class_id "
					 + "WHERE class.class_id = ?";

		try (PreparedStatement stmt = db.prepareStatement(showCategoryQuery)) {
			GradeManager.insertValues(stmt, currActiveClass);

			try (ResultSet rs = stmt.executeQuery()) {
				System.out.println("Categories for the active class:\n");

				while (rs.next()) {
					System.out.println(rs.getString("category_name") + "\t |" + rs.getInt("category_weight"));
				}
			}
		}
	}
	
	// add a new category

}
