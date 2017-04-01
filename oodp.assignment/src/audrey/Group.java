package audrey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import audrey.Enumerator.*;

public class Group implements Serializable {

	private int indexNo;
	private int vacancy;
	private ArrayList<Session> sessions;
	private ArrayList<Student> registered;
	private ArrayList<Student> waitlist;

	public Group()
	{
		this.indexNo = 0;
		this.vacancy = 0;
		this.sessions = new ArrayList<Session>();
		this.registered = new ArrayList<Student>();
		this.waitlist = new ArrayList<Student>();
	}

	public Group(int indexNo, int vacancy, ArrayList<Session> sessions)
	{
		this.indexNo = indexNo;
		this.vacancy = vacancy;
		this.sessions = sessions;
		this.registered = new ArrayList<Student>();
		this.waitlist = new ArrayList<Student>();
	}

	public Course getCourse(ArrayList<Course> courseList)
	{
		for (Course co : courseList)
		{
			if (!co.findGroup(indexNo).equals(null))
			{
				return co;
			}
		}
		return null;
	}

	public void addStudentToGroup(Student st)
	{
		if (vacancy > 0)
		{
			registered.add(st);
			vacancy--;
			System.out.println("Registering " + this.getIndexNo() + ".");
		}
		else
		{
			waitlist.add(st);
			System.out.println("Adding " + this.getIndexNo() + " to waitlist.");
		}
	}

	public void dropStudentFromGroup(Student st)
	{
		if (Enumerator.string(Group_Status.REGISTERED).equals(this.findStudent(st, "status")))
		{
			registered.remove(st);
			vacancy++;
			System.out.println("Removing " + this.getIndexNo() + " from course.");

			updateWaitlist();
		}
		else
		{
			waitlist.remove(st);
			System.out.println("Removing " + this.getIndexNo() + "from waitlist.");
		}
	}

	public Comparable findStudent(Student st, String returning)
	{
		Group_Status status = Group_Status.NOT_FOUND;
		for (Student s : registered)
		{
			if (s.equals(st))
			{
				status = Group_Status.REGISTERED;
			}
		}
		for (Student s : waitlist)
		{
			if (s.equals(st))
			{
				status = Group_Status.WAITLIST;
			}
		}
		return returning.equals("boolean") ? status != Group_Status.NOT_FOUND : Enumerator.string(status);
	}

	public void updateWaitlist()
	{
		System.out.println("Yet to Develop!"); //TODO:SMS/EMAIL
	}

	public void printStudents(int length, String delimiter)
	{
		String bar = Menu.getBar(48, "="), header = Menu.getTableHeader(length, delimiter, "student");
		System.out.println(bar + "\n" + header + "\n" + bar);
		for (Student st : registered)
		{
			st.printStudent(length, delimiter);
		}
		System.out.println(bar);
	}

	public void addSession(Session se)
	{
		boolean duplicate = false;
		for (Session s : sessions)
		{
			if (Objects.equals(s, se))
			{
				duplicate = true;
				System.out.println("Session already exist.");
			}
		}
		if (!duplicate)
		{
			sessions.add(se);
		} 
	}

	public int getIndexNo()
	{
		return indexNo;
	}

	public void setIndexNo(int indexNo)
	{
		this.indexNo = indexNo;
	}

	public int getVacancy()
	{
		return vacancy;
	}

	public void setVacancy(int vacancy)
	{
		this.vacancy = vacancy;
	}

	public ArrayList<Session> getSessions()
	{
		return sessions;
	}

	public void setSessions(ArrayList<Session> sessions)
	{
		this.sessions = sessions;
	}

	public ArrayList<Student> getRegistered()
	{
		return registered;
	}

	public void setRegistered(ArrayList<Student> registered)
	{
		this.registered = registered;
	}

	public ArrayList<Student> getWaitlist()
	{
		return waitlist;
	}

	public void setWaitlist(ArrayList<Student> waitlist)
	{
		this.waitlist = waitlist;
	}
}
