package audrey;

import java.util.Scanner;
import java.io.Console;
import java.util.ArrayList;
import java.util.Date;
import java.lang.String;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//java -Xmx1024m -jar run.jar
public class MySTARS {

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);
		ArrayList<User> user_list = new ArrayList<User>();
		ArrayList<Student> student_list = new ArrayList<Student>();
		ArrayList<Course> course_list = new ArrayList<Course>();

		RubbishData(user_list, student_list, course_list);

		int choice = check_student(in, user_list);
		switch (choice)
		{
			case 1:
				System.out.println("===== Student Menu =====");
				System.out.println("1. Add Course");
				System.out.println("2. Drop Course");
				System.out.println("3. Check/ Print Courses Registered");
				System.out.println("4. Check Vacancies");
				System.out.println("5. Change Index Number of Course");
				System.out.println("6. Swop Index Number with Another Student");
				System.out.println("0. Exit");
				break;
			case 2:
				System.out.println("===== Admin Menu =====");
				System.out.println("1. Edit Student Access Period");
				System.out.println("2. Add a Student");
				System.out.println("3. Add/ Update a Course");
				System.out.println("4. Check Available Slot for an Index Number");
				System.out.println("5. Print Student List by Index Number");
				System.out.println("6. Print Student List of Course");
				System.out.println("0. Exit");
				break;
			case 0:
				System.out.println("Wrong Password!");
				break;
			default:
				System.out.println("Not found!");
		}

		/** Write **/
		FileIO.writeSerializedObject("a_user", user_list);
		FileIO.writeSerializedObject("a_student", student_list);
		FileIO.writeSerializedObject("a_course", course_list);

		/** Read **/
		ArrayList<User> u2 = new ArrayList<User>();
		ArrayList<Student> s2 = new ArrayList<Student>();
		ArrayList<Course> c2 = new ArrayList<Course>();
		u2 = (ArrayList<User>) FileIO.readSerializedObject("a_user");
		s2 = (ArrayList<Student>) FileIO.readSerializedObject("a_student");
		c2 = (ArrayList<Course>) FileIO.readSerializedObject("a_course");

		for (User e : u2)
		{
			e.print();
		}
		for (Student e : s2)
		{
			e.print();
		}
		for (Course e : c2)
		{
			e.print();
		}
	}

	public static int check_student(Scanner in, ArrayList<User> ul)
	{
		Console console = System.console();
		User u;

		String username, password;
		System.out.print("Enter Username: ");
		username = in.nextLine();
		System.out.print("Enter Password: ");
		// password = String.copyValueOf(console.readPassword());
		password = in.nextLine();

		for (int i = 0; i < ul.size(); i++)
		{
			u = ul.get(i);
			if (u.getUsername().equalsIgnoreCase(username))
			{
				if (u.getPassword().equals(hash_password(password)))
				{
					if (ul.get(i) instanceof Student)
						return 1;
					else
						return 2;
				}
				else
					return 0;
			}
		}
		return -1;
	}

	public static String hash_password(String password)
	{
		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes(), 0, password.length());
			return (new BigInteger(1, md.digest()).toString());
		}
		catch (NoSuchAlgorithmException x)
		{
			// return x.toString();
			return "";
		}
	}

	public static void RubbishData(ArrayList<User> ul, ArrayList<Student> sl, ArrayList<Course> cl)
	{
		/* User */
		User u;
		u = new Admin("lll", hash_password("qwe"), "long long long");
		ul.add(u);

		u = new Student("audr", hash_password("abc"), "Audrey Ho", 'F', "U1621143E", 2, "11-12-95", "Singaporean",
				"93380356");
		ul.add(u);

		u = new Student("jess", hash_password("def"), "Jessie Wong", 'F', "U1234567R", 3, "02-04-92", "Singaporean",
				"92468215");
		ul.add(u);

		u = new Admin("qws", hash_password("dfg"), "qee wee see");
		ul.add(u);

		u = new Student("kelv", hash_password("ghi"), "Kelvin Ong", 'M', "U1637974T", 2, "23-09-96", "Singaporean",
				"97116342");
		ul.add(u);

		u = new Admin("erp", hash_password("rty"), "ele run path");
		ul.add(u);

		u = new Admin("tky", hash_password("iop"), "tan key yee");
		ul.add(u);

		u = new Student("moon", hash_password("jkl"), "Cheng Mun", 'F', "U1727964P", 1, "11-01-94", "Singaporean",
				"94651375");
		ul.add(u);

		/* Student */
		for (int i = 0; i < ul.size(); i++)
		{
			if (ul.get(i) instanceof Student)
				sl.add((Student) ul.get(i));
		}

		/* Course */
		Course c;
		c = new Course("cz2002", "oodp", 0, 3, "scse");
		cl.add(c);

		c = new Course("cz1012", "em2", 1, 2, "spms");
		cl.add(c);

		c = new Course("cz1001", "ens", 2, 2, "nie");
		cl.add(c);

	}
}