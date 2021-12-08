CREATE TABLE class (
  class_id SERIAL PRIMARY KEY,
  class_num VARCHAR(20) NOT NULL,
  class_term VARCHAR(20) NOT NULL,
  class_year INTEGER NOT NULL,
  class_sec_num INTEGER NOT NULL,
  class_description VARCHAR(500) NOT NULL
);

CREATE TABLE category (
  category_id SERIAL PRIMARY KEY,
  category_name VARCHAR(100) NOT NULL,
  category_weight INTEGER NOT NULL,

  class_id INTEGER NOT NULL REFERENCES class (class_id),

  UNIQUE (category_name, class_id)
);

CREATE TABLE assignments (
  assignments_id SERIAL PRIMARY KEY,
  assignments_name VARCHAR(100) NOT NULL,
  assignments_description TEXT NOT NULL,
  assignments_point_value INTEGER NOT NULL,
	
  category_id INTEGER NOT NULL REFERENCES category (category_id)
);

CREATE TABLE students (
  student_id INTEGER PRIMARY KEY,
  student_username VARCHAR(100) NOT NULL UNIQUE,
  student_name VARCHAR(100) NOT NULL
);

CREATE TABLE grades (
  grades_score INTEGER NOT NULL,
  assignments_id INTEGER NOT NULL REFERENCES assignments (assignments_id),
  student_id INTEGER NOT NULL REFERENCES students (student_id),
  PRIMARY KEY (student_id, assignments_id)
);

CREATE TABLE enroll (
  student_id INTEGER NOT NULL REFERENCES students (student_id),
  class_id INTEGER NOT NULL REFERENCES class (class_id),

  PRIMARY KEY (student_id, class_id)
);