package oodp_mystars;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;
import oodp_mystars.Enumerator.*;

/**
 * Represent the class where program start and main logic is located
 * 
 * @author Audrey KinSum Kelvin JianHao
 * @version 1.0
 * @since 2017-04-13
 */
// java -Xmx2048m -jar oodp_mystars.jar
public class MySTARS {

	public static void main(String[] args)
	{
		Scanner in = new Scanner(System.in);

		final String PARSE_DELIMITER = "| ";

		ArrayList<Logger> userList = new ArrayList<Logger>();
		ArrayList<Course> courseList = new ArrayList<Course>();

		Comparable user = null, peer = null;
		Course course = null;
		Group group1 = null, group2 = null;
		Session session;
		int option = 0, in_int1 = 0, in_int2 = 0, in_int3 = 0;
		int choice1 = 0, choice2 = 0;
		String in_string, bar, header;
		boolean alreadyExist, overlap;

		do
		{
			if (userList.isEmpty() || courseList.isEmpty())
			{
				FileIO.readData(userList, courseList);
			}

			Menu.printHeader(null);
			user = login(userList);

			if (user instanceof Admin)
			{
				do
				{
					Menu.printMenu(user);
					option = Formatter.getIntegerInput("Enter your option: ");
					switch (option)
					{
					case 1:
					{ // Edit Student Access Period
						Date start, end;

						System.out.println("Enter Start Date of Access Time");
						start = Formatter.enterDateTime("date_time");

						System.out.println("Enter End Date of Access Time");
						end = Formatter.enterDateTime("date_time");

						for (Logger lo : userList)
						{
							lo.setAccessSTime(start);
							lo.setAccessETime(end);
						}
						System.out.println("Access Time has been updated to " + Formatter.getPeriod(start, end, "dd-MM-yy hh:mm", " to "));

						FileIO.writeData(userList, courseList);
						break;
					}
					case 2:
					{ // Add a Student
						Logger newLogger = new Logger();
						Student newStudent = new Student();

						// Username
						System.out.print("Enter Username: ");
						newLogger.setUsername(in.next());
						// Password
						System.out.print("Enter Password: ");
						newLogger.setPassword(Formatter.hashPassword(in.next()));

						// Name
						System.out.print("Enter Name: ");
						in.nextLine();
						newStudent.setName(in.nextLine());
						// Gender
						newStudent.setGender(Enumerator.nextEnum(Gender.class));
						// Matric Number
						System.out.print("Enter Matric Number: ");
						newStudent.setMatricNo(in.next());
						// Study Year
						newStudent.setStudyYear(Formatter.getIntegerInput("Enter Study Year: "));
						// Email
						do
						{
							if (newStudent.getEmail() != null)
							{
								System.out.println("Please enter a valid Email Address.");
							}
							System.out.print("Enter Email Address: ");
							newStudent.setEmail(in.next());
						} while (!Checker.emailValid(newStudent.getEmail()));
						// Date of Birth
						System.out.println("Enter Date of Birth: ");
						newStudent.setDob(Formatter.enterDateTime("date"));
						// Nationality
						System.out.print("Enter Nationality: ");
						newStudent.setNationality(in.next());
						// Phone Number
						do
						{
							if (newStudent.getPhoneNo() != null)
							{
								System.out.println("Please enter a valid Local Phone Number.");
							}
							System.out.print("Enter Phone Number: ");
							newStudent.setPhoneNo(in.next());
						} while (newStudent.getPhoneNo().length() != 8);
						// Notification
						newStudent.setNotification(Enumerator.nextEnum(Notification_Status.class));

						newLogger.setAccessSTime(new Date());
						newLogger.setAccessETime(new Date());

						newLogger.setUser(newStudent);

						userList.add(newLogger);
						System.out.println("New user has been added");

						bar = Menu.getBorder(57, "=");
						header = Menu.getTableHeader(PARSE_DELIMITER, "student");
						System.out.println(bar + "\n" + header + "\n" + bar);
						for (Student st : getStudentList(userList))
						{
							st.printStudent(PARSE_DELIMITER);
							System.out.println();
						}
						System.out.println(bar);

						FileIO.writeData(userList, courseList);
						break;
					}
					case 3:
					{ // Add a Course
						course = new Course();
						// Id
						do
						{
							if (course.getId() != null)
							{
								System.out.println("Course Id " + course.getId().toUpperCase() + " has already been used.");
							}
							System.out.print("Enter Course Id: ");
							course.setId(in.next());
						} while (!Objects.equals(null, courseExist(courseList, course.getId())));
						// Name
						System.out.print("Enter Course Name: ");
						in.nextLine();
						course.setName(in.nextLine());
						// Type
						course.setType(Enumerator.nextEnum(Course_Type.class));
						// Credit
						course.setCredit(Formatter.getIntegerInput("Enter Number of Credits: "));
						// Number of Groups
						in_int1 = Formatter.withinRange("Number of Groups", 1, -1);
						// Number of Sessions
						in_int2 = Formatter.withinRange("Number of Sessions", 0, -1);

						while (course.getGroups().size() < in_int1)
						{
							System.out.println("Group #" + course.getGroups().size() + 1);
							group1 = new Group();
							// Index Number
							do
							{
								group1.setIndexNo(Formatter.getIntegerInput("Enter Index Number: "));
							} while (Checker.isDuplicate(courseList, group1.getIndexNo()));
							// Vacancy
							group1.setVacancy(Formatter.getIntegerInput("Enter Size of Group: "));

							while (group1.getSessions().size() < in_int2)
							{
								System.out.println("Group #" + course.getGroups().size() + 1
										+ " Session #" + group1.getSessions().size() + 1);
								if (group1.addSession(-1))
								{
									break;
								}
							}
							course.addGroup(group1);
						}
						courseList.add(course);
						System.out.println("New course has been added.");

						bar = Menu.getBorder(73, "=");
						header = Menu.getTableHeader(PARSE_DELIMITER, "course");
						System.out.println(bar + "\n" + header + "\n" + bar);
						for (Course c : courseList)
						{
							c.printCourse(PARSE_DELIMITER);
							System.out.println();
						}
						System.out.println(bar);

						FileIO.writeData(userList, courseList);
						break;
					}

					case 4:
					{ // Update a Course
						System.out.print("Enter Course Id: ");
						in_string = in.next();
						if (!Objects.equals(null, course = courseExist(courseList, in_string)))
						{
							bigloop: do
							{
								in_int1 = Formatter.getIntegerInput("\t1. Update Course Information\n"
										+ "\t2. Update Course Group\n"
										+ "\t0. Back to Menu\n"
										+ "Enter your choice: ");

								switch (in_int1)
								{
								case 1:
								{ // Update Course Information
									// Course Name
									System.out.println("Current Course Name: " + course.getName());
									System.out.print("New Course Name: ");
									in.nextLine();
									course.setName(in.nextLine());

									// Course Type
									System.out.println("Current Course Type: " + course.getType());
									course.setType(Enumerator.nextEnum(Course_Type.class));

									// Course Credits
									System.out.println("Current Credits: " + course.getCredit());
									course.setCredit(Formatter.getIntegerInput("New Credits: "));

									System.out.println("Course has been updated.");
									bar = Menu.getBorder(73, "=");
									header = Menu.getTableHeader(PARSE_DELIMITER, "course");
									System.out.println(bar + "\n" + header + "\n" + bar);
									course.printCourse(PARSE_DELIMITER);
									System.out.println();
									System.out.println(bar);

									FileIO.writeData(userList, courseList);
									break;
								}
								case 2:
								{ // Update Course Group
									do
									{
										in_int2 = Formatter.getIntegerInput("\t\t1. Add Group\n"
												+ "\t\t2. Edit Group\n"
												+ "\t\t3. Delete Group\n"
												+ "\t\t0. Back\n"
												+ "Enter your choice: ");
										switch (in_int2)
										{
										case 1:
										{ // Add Group
											alreadyExist = false;
											group1 = new Group();
											addGroupLoop: do
											{
												alreadyExist = false;
												group1.setIndexNo(Formatter.getIntegerInput("Enter Index Number: "));
												for (Course c : courseList)
												{
													Group checker = c.findGroup(group1.getIndexNo());
													if (checker != null)
													{
														alreadyExist = true;
														System.out.println("The Index Number " + group1.getIndexNo() + " already exist.");
													}
												}
											} while (alreadyExist);
											group1.setVacancy(Formatter.withinRange("Vancancies", 1, -1));
											course.addGroup(group1);
											System.out.println("Group has been added.");

											FileIO.writeData(userList, courseList);
											break;
										}
										case 2:
										{ // Edit Group
											editGroupLoop: do
											{
												System.out.println("Current Groups in Course Id: " + in_string.toUpperCase());
												for (int i = 0; i < course.getGroups().size(); i++)
												{
													System.out.println("\t" + (i + 1) + ". " + course.getGroups().get(i).getIndexNo());
												}
												System.out.println("\t0. Back");
												if ((choice1 = Formatter.withinRange("choice", 0,
														course.getGroups().size())) != 0)
												{
													group1 = course.getGroups().get(choice1 - 1);
													in_int3 = Formatter.getIntegerInput("\t\t\t1. Edit Group Vacancy\n"
															+ "\t\t\t2. Add Session\n"
															+ "\t\t\t3. Edit Session\n"
															+ "\t\t\t4. Delete Session\n"
															+ "\t\t\t0. Back\n"
															+ "Enter your choice: ");
													switch (in_int3)
													{
													case 1:
													{ // Edit Group Vacancy
														System.out.println("Current Vacancy in group " + group1.getIndexNo() + ": " + group1.getVacancy());
														group1.setVacancy(Formatter.withinRange("Vacancies", group1.getRegistered().size(), -1));
														System.out.println("Vacancy has been changed to : " + group1.getVacancy());
														break editGroupLoop;
													}
													case 2:
													{ // Add Session
														if (group1.addSession(-1))
														{
															System.out.println("Session has been added.");

															FileIO.writeData(userList, courseList);
														}
														break editGroupLoop;
													}
													case 3:
													{ // Edit Session
														System.out.println("Current Session in Course Id: " + in_string.toUpperCase()
																+ " Index Number: " + group1.getIndexNo());
														for (int i = 0; i < group1.getSessions().size(); i++)
														{
															session = group1.getSessions().get(i);
															System.out.println("\t\t\t" + (i + 1)
																	+ ". Type: " + session.getType()
																	+ " Venue: " + session.getVenue()
																	+ " Day: " + session.getDay()
																	+ " Time: " + Formatter.getPeriod(session.getSTime(), session.getETime(), "hh:mm", " - "));
														}
														System.out.println("\t\t\t0. Back");
														if ((choice2 = Formatter.withinRange("choice", 0, group1.getSessions().size())) != 0)
														{
															if (group1.addSession(choice2 - 1))
															{
																System.out.println("Session is updated.");

																FileIO.writeData(userList, courseList);
															}
														}
														break editGroupLoop;
													}
													case 4:
													{ // Delete Session
														System.out.println("Current Session in Course Id: " + in_string.toUpperCase()
																+ " Index Number: " + group1.getIndexNo());
														for (int i = 0; i < group1.getSessions().size(); i++)
														{
															session = group1.getSessions().get(i);
															System.out.println("\t\t\t" + (i + 1)
																	+ ". Type: " + session.getType()
																	+ " Venue: " + session.getVenue()
																	+ " Day: " + session.getDay()
																	+ " Time: " + Formatter.getPeriod(session.getSTime(), session.getETime(), "hh:mm", "-"));
														}
														System.out.println("\t\t\t0. Back");
														if ((choice2 = Formatter.withinRange("choice", 0, group1.getSessions().size())) != 0)
														{
															group1.getSessions().remove(choice2 - 1);
															System.out.println("Session has been deleted.");

															FileIO.writeData(userList, courseList);
														}
														break editGroupLoop;
													}
													case 0:
													{
														break editGroupLoop;
													}
													}
												}
											} while (in_int3 != 0);
											break;
										}
										case 3:
										{ // Delete Group
											System.out.println("Groups in CourseID: " + in_string);
											for (int i = 0; i < course.getGroups().size(); i++)
											{
												System.out.println("\t" + (i + 1) + ". " + course.getGroups().get(i).getIndexNo());
											}
											System.out.println("\t0. Back");
											if ((choice1 = Formatter.withinRange("choice", 0, course.getGroups().size())) != 0)
											{
												if (course.getGroups().get(choice1 - 1).getRegistered().size() == 0)
												{
													course.getGroups().remove(choice1 - 1);
													System.out.println("Group has been deleted.");

													FileIO.writeData(userList, courseList);
												}
												else
												{
													System.out.println("Not allowed to delete group with students inside");
												}
											}
											break;
										}
										case 0:
										{
											break;
										}
										default:
										{
											System.out.println("Invalid choice.");
											break;
										}
										}
									} while (in_int2 != 0);
									break;
								}
								case 0:
								{
									break;
								}
								default:
								{
									System.out.println("Invalid choice.");
									break;
								}
								}
							} while (in_int1 != 0);
						}
						else
						{
							System.out.println("Course Id " + in_string.toUpperCase() + " does not exist.");
						}
						break;
					}
					case 5:
					{ // Check Available Slot for an Index Number
						in_int1 = Formatter.getIntegerInput("Enter Index Number of Group: ");
						if (!Objects.equals(null, group1 = groupExist(courseList, in_int1)))
						{
							System.out.println("Index Number " + in_int1 + " has " + group1.getVacancy() + " vacancies.");
						}
						else
						{
							System.out.println("Index Number " + in_int1 + " is not found.");
						}
						break;
					}
					case 6:
					{ // Print Student List by Index Number
						in_int1 = Formatter.getIntegerInput("Enter Index Number of Group: ");

						if (!Objects.equals(null, group1 = groupExist(courseList, in_int1)))
						{
							if (group1.getRegistered().size() > 0)
							{
								bar = Menu.getBorder(57, "=");
								header = Menu.getTableHeader(PARSE_DELIMITER, "student");
								System.out.println(bar + "\n" + header + "\n" + bar);
								for (Student std : getStudentList(userList))
								{
									for (String registered : group1.getRegistered())
									{
										if (std.getMatricNo().equalsIgnoreCase(registered))
										{
											std.printStudent(PARSE_DELIMITER);
											System.out.println();
										}
									}
								}
								System.out.println(bar);
							}
							else
							{
								System.out.println("Index Number " + in_int1 + " has no students.");
							}
						}
						else
						{
							System.out.println("Index Number " + in_int1 + " is not found.");
						}
						break;
					}
					case 7:
					{ // Print Student List of Course
						System.out.print("Enter Course Id: ");
						in_string = in.next();

						if (!Objects.equals(null, course = courseExist(courseList, in_string)))
						{
							if (course.getStudentList().size() > 0)
							{
								bar = Menu.getBorder(57, "=");
								header = Menu.getTableHeader(PARSE_DELIMITER, "student");
								System.out.println(bar + "\n" + header + "\n" + bar);
								for (String registered : course.getStudentList())
								{
									for (Student student : getStudentList(userList))
									{
										if (student.getMatricNo().equalsIgnoreCase(registered))
										{
											student.printStudent(PARSE_DELIMITER);
											System.out.println();
										}
									}
								}
								System.out.println(bar);
							}
							else
							{
								System.out.println("Course Id " + in_string.toUpperCase() + " has no students.");
							}
						}
						else
						{
							System.out.println("Course Id " + in_string.toUpperCase() + " is not found.");
						}
						break;
					}
					case 0:
					{ // Exit
						FileIO.writeData(userList, courseList);

						System.out.println("Exiting.");
						break;
					}
					default:
					{
						System.out.println("Option entered is invalid.");
						break;
					}
					}
				} while (option != 0);
			}
			else if (user instanceof Student)
			{
				Student student = (Student) user;
				do
				{
					Menu.printMenu(user);
					option = Formatter.getIntegerInput("Enter your option: ");
					switch (option)
					{
					case 1:
					{ // Add Course
						in_int1 = Formatter.getIntegerInput("Enter Index Number of Group: ");

						alreadyExist = false;
						if (!Objects.equals(null, group1 = groupExist(courseList, in_int1)))
						{
							course = group1.getCourse(courseList);

							for (Group cGroup : course.getGroups())
							{
								if (student.findGroup(cGroup.getIndexNo()) != -1)
								{
									alreadyExist = true;
									break;
								}
							}
							if (!alreadyExist)
							{
								overlap = false;
								checkOverlapLoop: for (int registeredIndex : student.getRegisteredGroup())
								{
									for (Course c : courseList)
									{
										if (c.findGroup(registeredIndex) == null)
										{
											break;
										}
										else
										{
											if (Checker.overlapGroup(c.findGroup(registeredIndex), group1, null))
											{
												overlap = true;
												break checkOverlapLoop;
											}
										}
									}
								}

								if (!overlap)
								{
									System.out.println(Formatter.tabs(5, "", "Index Number: " + group1.getIndexNo())
											+ "Course: " + course.getId());
									course.printGroups(group1.getIndexNo(), -1, PARSE_DELIMITER);
									System.out.println("Course Type: " + course.getType());

									System.out.print("Confirm to Add Course? (Y/N) ");
									if (in.next().equalsIgnoreCase("Y"))
									{
										if ((group1.addStudentToGroup(student.getMatricNo())).equals(Group_Status.REGISTERED))
										{
											System.out.println("Registered to Index Number " + group1.getIndexNo() + ".");
										}
										else
										{
											System.out.println("Adding to Index Number " + group1.getIndexNo() + "'s waitlist.");
										}
										student.getRegisteredGroup().add(group1.getIndexNo());

										FileIO.writeData(userList, courseList);
									}
									else
									{
										System.out.println("Index Number " + in_int1 + " is not added.");
									}
								}
								else
								{
									System.out.println("Overlaps with an group already registered.");
								}
							}
							else
							{
								System.out.println("Course has already been registered.");
							}
						}
						else
						{
							System.out.println("Index Number " + in_int1 + " do not exist.");
						}
						break;
					}
					case 2:
					{ // Drop Course
						if (student.getRegisteredGroup().size() > 0)
						{
							student.listCourses(courseList);
							if ((in_int1 = Formatter.withinRange("index number", 0, student.getRegisteredGroup().size())) > 0)
							{
								group1 = groupExist(courseList, student.getRegisteredGroup().get(in_int1 - 1));
								course = group1.getCourse(courseList);

								System.out.println(Formatter.tabs(5, "", "Index Number: " + group1.getIndexNo())
										+ "Course: " + course.getId());
								course.printGroups(group1.getIndexNo(), -1, PARSE_DELIMITER);
								System.out.println(Formatter.tabs(5, "", "Course Type: " + course.getType())
										+ "Status: " + group1.findStudentStatus(student.getMatricNo()));

								System.out.print("Confirm to Drop Course? (Y/N) ");

								if (in.next().equalsIgnoreCase("Y"))
								{
									if (group1.dropStudent(student.getMatricNo()).equals(Group_Status.REGISTERED))
									{
										System.out.println("Removing student from " + group1.getIndexNo() + "'s registered list.");
										group1.updateWaitlist(getStudentList(userList), courseList);
									}
									else
									{
										System.out.println("Removing student from " + group1.getIndexNo() + "'s waitlist.");
									}
									student.getRegisteredGroup().remove(in_int1 - 1);

									FileIO.writeData(userList, courseList);
								}
								else
								{
									System.out.println("Index Number " + in_int1 + " is not dropped.");
								}
							}
						}
						else
						{
							System.out.println("No courses has been added.");
						}
						break;
					}
					case 3:
					{ // Check/Print Courses Registered
						if (student.getRegisteredGroup().size() > 0)
						{
							student.printCourses(courseList, PARSE_DELIMITER);
						}
						else
						{
							System.out.println("No courses has been added.");
						}
						break;
					}
					case 4:
					{ // Check Vacancies Available
						in_int1 = Formatter.getIntegerInput("Enter Index Number of Group: ");
						do
						{
							if (!Objects.equals(null, group1 = groupExist(courseList, in_int1)))
							{
								course = group1.getCourse(courseList);
								System.out.println(Formatter.tabs(5, "", "Index Number: " + group1.getIndexNo())
										+ "Course: " + course.getId());
								course.printGroups(group1.getIndexNo(), -1, PARSE_DELIMITER);
								System.out.println(Formatter.tabs(5, "", "Places Available: " + group1.getVacancy())
										+ "Length of WaitList: " + group1.getWaitlist().size());
							}
							else
							{
								System.out.println("Index Number " + in_int1 + " do not exist.");
							}

							in_int1 = Formatter.getIntegerInput("\nCheck Vacancy of another Index Number"
									+ "\nIndex Number (or 0. Back to Menu): ");
						} while (in_int1 != 0);
						break;
					}
					case 5:
					{ // Change Index Number of Group
						in_int1 = Formatter.getIntegerInput("Enter Current Index Number of Group: ");

						for (Course c : courseList)
						{
							if (!Objects.equals(null, group1 = c.findGroup(in_int1)))
							{
								break;
							}
						}

						if (group1 != null)
						{
							if (student.getRegisteredGroup().contains(group1.getIndexNo()))
							{
								if (Enumerator.string(Group_Status.REGISTERED)
										.equals(group1.findStudentStatus(student.getMatricNo())))
								{
									in_int2 = Formatter.getIntegerInput("Enter New Index Number of Group: ");
									if (in_int1 != in_int2)
									{
										course = group1.getCourse(courseList);
										if (!Objects.equals(null, group2 = course.findGroup(in_int2)))
										{
											if (group2.getVacancy() > 0)
											{
												overlap = false;
												for (Course c : courseList)
												{
													for (Group g1 : c.getGroups())
													{
														for (int rg : student.getRegisteredGroup())
														{
															if (rg == g1.getIndexNo())
															{
																overlap = Checker.overlapGroup(g1, group2, course.findGroup(in_int1));
																if (overlap)
																{
																	break;
																}
															}
														}
													}
												}
												if (!overlap)
												{
													if (group2.getIndexNo() != group1.getIndexNo())
													{
														System.out.println("Subject: " + course.getId());
														System.out.println(Formatter.tabs(14, "", "Current Index Number: " + String.valueOf(group1.getIndexNo()))
																+ "New Index Number: " + String.valueOf(group2.getIndexNo()));
														course.printGroups(group1.getIndexNo(), group2.getIndexNo(), PARSE_DELIMITER);
														System.out.println(Formatter.tabs(14, "", "Subject Type: " + course.getType()) + "Status: " + Enumerator.string(Group_Status.REGISTERED));
														System.out.print("Confirm to Change Index Number? (Y/N) ");
														if (in.next().equalsIgnoreCase("Y"))
														{
															group1.dropStudent(student.getMatricNo());
															group1.updateWaitlist(getStudentList(userList), courseList);
															group2.addStudentToGroup(student.getMatricNo());

															student.replaceGroup(group1.getIndexNo(), group2.getIndexNo());

															System.out.println("Index Number " + group1.getIndexNo() + " has been changed to " + group2.getIndexNo());

															FileIO.writeData(userList, courseList);
														}
														else
														{
															System.out.println("Index Number " + in_int1 + " is not changed.");
														}
													}
													else
													{
														System.out.println("You are already in this group.");
													}
												}
												else
												{
													System.out.println("Overlaps with an group already registered.");
												}
											}
											else
											{
												System.out.println("Index Number " + in_int2 + " has no vacancy.");
											}
											break;
										}
										else
										{
											if (!Objects.equals(null, groupExist(courseList, in_int2)))
											{
												System.out.println("Index Number " + in_int2 + " exist in a different Course.");
											}
											else
											{
												System.out.println("Index Number " + in_int2 + " do not exist.");
											}
										}
									}
									else
									{
										System.out.println("Current and New Index Number is the same.");
									}
								}
								else
								{
									System.out.println("Index Number " + in_int1 + " is in waitlist.");
								}
							}
							else
							{
								if (!Objects.equals(null, groupExist(courseList, in_int1)))
								{
									System.out.println("Index Number " + in_int1 + " is not registered.");
								}
								else
								{
									System.out.println("Index Number " + in_int1 + " do not exist.");
								}
							}
						}
						else
						{
							System.out.println("Course does not exist");
						}
						break;
					}
					case 6:
					{ // Swap Index Number with Another Student
						in_int1 = Formatter.getIntegerInput("Enter your Index Number: ");

						for (Course c : courseList)
						{
							group1 = c.findGroup(in_int1);
							if (group1 != null)
							{
								course = group1.getCourse(courseList);
								break;
							}
						}
						if (student.getRegisteredGroup().contains(in_int1))
						{
							if (Enumerator.string(Group_Status.REGISTERED).equals(group1.findStudentStatus(student.getMatricNo())))
							{
								System.out.println("Please ask your Peer to Log in");
								if (!Objects.equals(null, peer = login(userList)) && peer instanceof Student)
								{
									in_int2 = Formatter.getIntegerInput("Enter Peer's Index Number: ");
									if (((Student) peer).getRegisteredGroup().contains(in_int2))
									{
										for (Course c : courseList)
										{
											group2 = c.findGroup(in_int2);
											if (group2 != null)
											{
												break;
											}
										}
										if (Enumerator.string(Group_Status.REGISTERED).equals(group2.findStudentStatus(((Student) peer).getMatricNo())))
										{
											//Check for different course
											if (group1.getCourse(courseList).getId().equalsIgnoreCase(group2.getCourse(courseList).getId()))
											{
												// Check overlap
												overlap = false;
												overlapCheckLoop: for (Course c : courseList)
												{
													for (Group g1 : c.getGroups())
													{
														for (int rg : student.getRegisteredGroup())
														{
															if (rg == g1.getIndexNo())
															{
																overlap = Checker.overlapGroup(g1, group2, c.findGroup(in_int1));
																if (overlap)
																{
																	System.out.println("Student " + student.getMatricNo() + " has a group that overlaps.");
																	break overlapCheckLoop;
																}
															}
														}

														for (int rg : ((Student) peer).getRegisteredGroup())
														{
															if (rg == g1.getIndexNo())
															{
																overlap = Checker.overlapGroup(group2, group1, c.findGroup(in_int2));
																if (overlap)
																{
																	System.out.println("Student " + ((Student) peer).getMatricNo() + " has a group that overlaps.");
																	break overlapCheckLoop;
																}
															}
														}
													}
												} // Overlapcheck loop
												if (!overlap)
												{
													if (group1.getIndexNo() != group2.getIndexNo())
													{
														System.out.println("Subject: " + course.getId());
														System.out.println(Formatter.tabs(14, "", "Student #1 ") + "Student #2");
														System.out.println(
																Formatter.tabs(14, "", "Matric Number: " + student.getMatricNo() + Menu.getBorder(16, " ") + "Index Number: " + group1.getIndexNo())
																		+ "Matric Number: " + ((Student) peer).getMatricNo() + Menu.getBorder(16, " ") + "Index Number: " + group2.getIndexNo());
														course.printGroups(group1.getIndexNo(), group2.getIndexNo(), PARSE_DELIMITER);
														System.out.println(Formatter.tabs(14, "", "Subject Type: " + course.getType()) + "Subject Type: " + course.getType());
														System.out.print("Confirm to Swop Index Number? (Y/N) ");
														if (in.next().equalsIgnoreCase("Y"))
														{
															group1.dropStudent(student.getMatricNo());
															group2.dropStudent(((Student) peer).getMatricNo());

															group1.addStudentToGroup(((Student) peer).getMatricNo());
															group2.addStudentToGroup(student.getMatricNo());

															student.replaceGroup(group1.getIndexNo(), group2.getIndexNo());
															((Student) peer).replaceGroup(group2.getIndexNo(), group1.getIndexNo());

															System.out.println(student.getMatricNo() + "-Index Number " + group1.getIndexNo() + " has successfully swopped with "
																	+ ((Student) peer).getMatricNo() + "-Index Number " + group2.getIndexNo());

															FileIO.writeData(userList, courseList);
														}
														else
														{
															System.out.println(student.getMatricNo() + " and " + ((Student) peer).getMatricNo() + " is not swoped.");
														}
													}
													else
													{
														System.out.println("Already registered in this group.");
													}
												}
											}
											else
											{
												System.out.println("The groups are in different courses");
											}
										}
										else
										{
											System.out.println("Student " + ((Student) peer).getMatricNo() + "'s Course Index Number " + in_int2 + " is in waitlist.");
										}
									}
									else
									{
										System.out.println("Peer is not in the group: " + in_int2);
									}
								}
								else
								{
									System.out.println("The username or password you entered is incorrect.");
								}
							}
							else
							{
								System.out.println("Student " + student.getMatricNo() + "'s Course Index Number " + in_int1 + " is in waitlist.");
							}
						}
						else
						{
							if (!Objects.equals(null, groupExist(courseList, in_int1)))
							{
								System.out.println("Student " + student.getMatricNo() + " is not registerd in Index Number " + in_int1 + ".");
							}
							else
							{
								System.out.println("Index Number " + in_int1 + " do not exist.");
							}
						}

						break;
					}
					case 7:
					{ // Update Particulars
						in_int1 = Formatter.getIntegerInput("\t1. Email\n"
								+ "\t2. Phone Number\n"
								+ "\t3. Notification Status\n"
								+ "\t4. Password\n"
								+ "\t0. Back to Menu\n"
								+ "Choose your option: ");

						switch (in_int1)
						{
						case 1:
						{ // Email
							System.out.println("Current Email: " + student.getEmail());
							System.out.print("Enter the new Email: ");
							in_string = in.next();
							if (Checker.emailValid(in_string))
							{
								student.setEmail(in_string);
								System.out.println("Email has been updated successfully.");

								FileIO.writeData(userList, courseList);
							}
							else
							{
								System.out.println(in_string + " is not valid Email.");
							}
							break;
						}
						case 2:
						{ // Phone Number
							System.out.println("Current Phone Number: " + student.getPhoneNo());
							System.out.print("Enter the new Phone Number: ");
							in_string = in.next();
							if (in_string.length() == 8)
							{
								student.setPhoneNo(in_string);
								System.out.println("Phone Number has been updated successfully.");

								FileIO.writeData(userList, courseList);
							}
							else
							{
								System.out.println(in_string + " is not valid Local Phone Number.");
							}
							break;
						}
						case 3:
						{ // Notification Status
							System.out.println("Current Notification Status: " + student.getNotification());
							student.setNotification(Enumerator.nextEnum(Notification_Status.class));
							System.out.println("Notification Status has been updated successfully.");

							FileIO.writeData(userList, courseList);
							break;
						}
						case 4:
						{ // Password
							Logger logger = null;
							String pass;
							for (Logger lo : userList)
							{
								if (Objects.equals(lo.getUser(), user))
								{
									logger = lo;
								}
							}
							System.out.print("Enter your current Password: ");
							if (logger.getPassword().equals(inputPassword()))
							{
								System.out.print("Enter your new Password: ");
								logger.setPassword(inputPassword());
								System.out.println("Password has been updated successfully.");

								FileIO.writeData(userList, courseList);
							}
							else
							{
								System.out.println("Current Password entered incorrectly.");
							}
							break;
						}
						}
						break;
					}
					case 0:
					{ // Exit
						FileIO.writeData(userList, courseList);

						System.out.println("Exiting.");
						break;
					}
					default:
					{
						System.out.println("Option entered is invalid.");
						break;
					}
					}
				} while (option != 0);
			}
			else
			{
				System.out.println("The username or password you entered is incorrect.");
			}

			System.out.print("Next user login? (Y/N) ");

		} while (in.next().equalsIgnoreCase("Y"));

	}

	/**
	 * Checks user credentials is entered correctly, and if student can access
	 * 
	 * @param userList
	 *            list of users
	 * @return Logger account if credentials entered correctly/ null if entered wrongly
	 */
	public static Comparable login(ArrayList<Logger> userList)
	{
		String username, password;

		System.out.print("Username: ");
		username = (new Scanner(System.in)).next();
		System.out.print("Password: ");
		password = inputPassword();

		for (Logger lo : userList)
		{
			if (lo.getUsername().equalsIgnoreCase(username))
			{
				if (lo.getPassword().equals(password))
				{
					if (lo.getUser() instanceof Student)
					{
						Date today = new Date();
						if (today.after(lo.getAccessSTime()) && today.before(lo.getAccessETime()))
						{
							return lo.getUser();
						}
						else
						{
							System.out.println("You can only access between "
									+ Formatter.getPeriod(lo.getAccessSTime(), lo.getAccessETime(), "dd-MM-yyyy hh:mm a", " to "));
							return null;
						}
					}
					else
					{
						return lo.getUser();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets students from user list
	 * 
	 * @param userList
	 *            list of all the users
	 * @return list of students
	 */
	public static ArrayList<Student> getStudentList(ArrayList<Logger> userList)
	{
		ArrayList<Student> std = new ArrayList<Student>();
		for (Logger lo : userList)
		{
			if (lo.getUser() instanceof Student)
			{
				std.add(((Student) lo.getUser()));
			}
		}
		return std;
	}

	/**
	 * Checks if course exist
	 * 
	 * @param courseList
	 *            list of all the course
	 * @param courseId
	 *            course id that is searching for
	 * @return course that is looking for/ null if course cant be found
	 */
	public static Course courseExist(ArrayList<Course> courseList, String courseId)
	{
		for (Course co : courseList)
		{
			if (co.getId().equalsIgnoreCase(courseId))
			{
				return co;
			}
		}
		return null;
	}

	/**
	 * Checks if group exist
	 * 
	 * @param courseList
	 *            list of all the course
	 * @param indexNo
	 *            group index number that is searching for
	 * @return group that is looking for/ null if group cant be found
	 */
	public static Group groupExist(ArrayList<Course> courseList, int indexNo)
	{
		Group gr;
		for (Course co : courseList)
		{
			if (!Objects.equals(null, gr = co.findGroup(indexNo)))
			{
				return gr;
			}
		}
		return null;
	}

	/**
	 * Get the password based on input
	 * 
	 * @return hashed password
	 */
	public static String inputPassword()
	{
		return Formatter.hashPassword(String.copyValueOf(System.console().readPassword()));
		//return Formatter.hashPassword((new Scanner(System.in)).next());
	}
}