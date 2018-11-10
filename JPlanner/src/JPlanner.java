import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;

import com.mindfusion.common.DateTime;
import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.CalendarAdapter;
import com.mindfusion.scheduling.ItemMouseEvent;
import com.mindfusion.scheduling.Selection;
import com.mindfusion.scheduling.model.Appointment;
import com.mindfusion.scheduling.model.Contact;
import com.mindfusion.scheduling.model.ContentType;
import com.mindfusion.scheduling.model.Item;
import com.mindfusion.scheduling.model.RemoveItemCommand;
import com.mindfusion.scheduling.model.Schedule;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.mindfusion.scheduling.ItemConfirmEvent;
import com.mindfusion.scheduling.model.ItemEvent;
import com.mindfusion.scheduling.ResourceDateEvent;

public class JPlanner {

	private JFrame frame;
	private String dataFile;
	private Calendar cal = new Calendar();
	private Contact c;
	Schedule s = new Schedule();
	JList<String> employeeList = new JList<String>(new SortedListModel());
	JList<String> patientList = new JList<String>(new SortedListModel());
	JPanel employeePanel, patientPanel;
	boolean employeeNotClicked = true, patientNotClicked = true;
	File employeeFile = new File("employees.txt");
	File patientFile = new File("patients.txt");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JPlanner window = new JPlanner();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JPlanner() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Main frame to put everything in
		frame = new JFrame();
		frame.setBounds(100, 100, 1300, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		
		// Panel for top buttons and combo box
		JPanel buttonPanel = new JPanel();
		frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.setLayout(new GridLayout(0, 3, 0, 0));

		
		
		// Add new employee button
		JButton btnAddEmployee = new JButton("Add Employee");
		btnAddEmployee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEmployee();

			}

		});
		buttonPanel.add(btnAddEmployee);

		
		
		// Add new patient button
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPatient();
			}
		});
		buttonPanel.add(btnAddPatient);

		
		
		// Drop down to choose calendar view
		JComboBox viewTypeCombo = new JComboBox();
		viewTypeCombo.setToolTipText("View Type");
		buttonPanel.add(viewTypeCombo);
		viewTypeCombo.addItem("Monthly View");
		viewTypeCombo.addItem("Weekly View");
		viewTypeCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				if(e.getItem().toString().equals("Monthly View")) {
					cal.setCurrentView(com.mindfusion.scheduling.CalendarView.SingleMonth);	
				}
				else if(e.getItem().toString().equals("Weekly View")) {
					cal.beginInit();
					cal.setCurrentView(com.mindfusion.scheduling.CalendarView.Timetable);					
					cal.getTimetableSettings().getDates().clear();
					
					for (int i = 0; i < 7; i++) {
					    cal.getTimetableSettings().getDates().add(DateTime.today().addDays(i));
					}
					cal.getTimetableSettings().setVisibleColumns(7);
					cal.endInit();
				}
				
			}
		});
		
		

		
		
		// Panel for employees to be listed on right side of panel
		employeePanel = new JPanel();
		frame.getContentPane().add(employeePanel, BorderLayout.EAST);
		employeePanel.setLayout(new BorderLayout(0, 0));
		JLabel lblEmployee = new JLabel("Employee");
		employeePanel.add(lblEmployee, BorderLayout.NORTH);
		employeePanel.add(employeeList, BorderLayout.CENTER);

		

		// Panel for patients to be listed on left side of panel
		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel, BorderLayout.WEST);
		patientPanel.setLayout(new BorderLayout(0, 0));
		JLabel lblPatient = new JLabel("Patients");
		patientPanel.add(lblPatient, BorderLayout.NORTH);
		patientPanel.add(patientList, BorderLayout.CENTER);

		
		
		// Make Calendar panel
		JPanel calPanel = new JPanel();
		frame.getContentPane().add(calPanel, BorderLayout.CENTER);
		calPanel.setLayout(new GridLayout(1, 0, 0, 0));

		
		
		// Display calendar
		calPanel.add(cal);
		cal.setCurrentView(com.mindfusion.scheduling.CalendarView.SingleMonth);
		cal.setAllowInplaceCreate(false);
		cal.setShowToolTips(true);
		cal.setInteractiveItemType(Shift.class);
		Schedule.registerItemClass(Shift.class, "shift", 1);


		cal.addCalendarListener(new CalendarAdapter() {

			@Override
			public void itemClick(ItemMouseEvent e) {

				//Right click to delete shift
				if (e.getButton() == MouseEvent.BUTTON3) {
					// Warning message before delete
					if (JOptionPane.showConfirmDialog(null, "Delete?", "Warning", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE) == 0) {
						Item i = e.getItem();
						cal.getSchedule().getItems().remove(i); // This is how you delete an item

					}
				}
			}

			@Override
			public void dateClick(ResourceDateEvent e) {
				
				//Double click on date to bring up add shift window
				if (e.getClicks() == 2) {
				
					JOptionPane addShift = new JOptionPane(new AddShift(employeeList), JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					JDialog d = new JDialog();
					d.add(addShift);
					d.show();
					cal.update();
					cal.resetDrag();
					
				}
				
			}
		});

		
		
		//Load schedule from default file on window open and save to dataFile on window close
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cal.getSchedule().saveTo(dataFile, ContentType.Xml);
			}

			public void windowOpened(WindowEvent e) {
				File f = new File("data.dat");
				dataFile = f.getAbsolutePath();
				System.out.println("datafile: " + dataFile);
				if (!f.exists()) {
					try {
						f.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					cal.getSchedule();
				} else {
					System.out.println("Already exists");
					cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
				}

				loadEmployeeFile();
				loadPatientFile();
			}
		});
		
		
		
		//Show employee schedule in calendar when you click on name
		employeeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {

				if (employeeNotClicked) {
					employeeNotClicked = false;
				} else {
					cal.getSchedule().saveTo(dataFile, ContentType.Xml);
				}
				employeeListValueChanged(evt);
			}

		});

		deleteSchedule(employeeList);
		
		
		
		//Show patient schedule in calendar when you click on name
		patientList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if (patientNotClicked) {
					patientNotClicked = false;
				} else {
					cal.getSchedule().saveTo(dataFile, ContentType.Xml);
				}

				patientListValueChanged(evt);
			}
		});

		deleteSchedule(patientList);
		
	}

	/**
	 * TODO: patient name still adds when you only type in a first name and the
	 * warning box pops up and you press okay
	 */
	private void addPatient() {
		boolean canAdd = true;
		JTextField firstName = new JTextField();
		JTextField lastName = new JTextField();

		Object[] fields = { "First Name: ", firstName, "Last Name: ", lastName };
		JOptionPane addPatPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		;
		if (JOptionPane.showConfirmDialog(null, fields, "Add Patient", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE) == 0) {
			System.out.println("We're in");
			if (firstName.getText().equals("") || lastName.getText().equals("")) {
				System.out.println("We're still in");

				JDialog d = addPatPane.createDialog(null, "Warning");
				d.setVisible(true);
				canAdd = false;
			}
			if (firstName.getText() != "" && lastName.getText() != "" && canAdd == true) {

				Patient p = new Patient();
				p.setFirstName(firstName.getText());
				p.setLastName(lastName.getText());

				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter("patients.txt", true));
					writer.write(p.getFirstName() + ";" + p.getLastName() + "\n");
					writer.close();
				} catch (IOException o) {
					// TODO Auto-generated catch block
					o.printStackTrace();
				}

				((SortedListModel) patientList.getModel()).add(p.getFirstName() + " " + p.getLastName());
				File f = new File(p.getFirstName() + " " + p.getLastName() + "data.dat");
				try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else {
				System.out.println("Must enter first and last name of patient.");

			}
		}

	}

	/**
	 * TODO: employee name still adds when you only type in a first name and the
	 * warning box pops up and you press okay
	 */
	private void addEmployee() {
		boolean canAdd = true;
		JTextField firstName = new JTextField();
		JTextField lastName = new JTextField();

		Object[] fields = { "First Name: ", firstName, "Last Name: ", lastName };
		JOptionPane addEmpPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		;
		if (JOptionPane.showConfirmDialog(null, fields, "Add Employee", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE) == 0) {
			System.out.println("We're in");
			if (firstName.getText().equals("") || lastName.getText().equals("")) {
				System.out.println("We're still in");
				JDialog d = addEmpPane.createDialog(null, "Warning");
				d.setVisible(true);
				canAdd = false;
			}
			if (firstName.getText() != "" && lastName.getText() != "" && canAdd == true) {

				Employee e = new Employee();
				e.setFirstName(firstName.getText());
				e.setLastName(lastName.getText());

				try {
					//BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt", true));
					BufferedWriter writer = new BufferedWriter(new FileWriter(employeeFile, true));
					writer.write(e.getFirstName() + ";" + e.getLastName() + "\n");
					writer.close();
				} catch (IOException o) {
					// TODO Auto-generated catch block
					o.printStackTrace();
				}

				((SortedListModel) employeeList.getModel()).add(e.getFirstName() + " " + e.getLastName());
				File f = new File(e.getFirstName() + " " + e.getLastName() + "data.dat");
				try {
					f.createNewFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else {
				System.out.println("Must enter first and last name of employee.");
			}
		}

	}

	/**
	 * Show the schedule of the employee that has been clicked on
	 */
	private void employeeListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		cal.update();
		// set text on right here
		String s = (String) employeeList.getSelectedValue();
		for (int i = 0; i < employeeList.getModel().getSize(); i++) {
			if (s.equals(employeeList.getModel().getElementAt(i))) {

				//System.out.println("Selected value:" + s);
				//System.out.println("Element at i:" + employeeList.getModel().getElementAt(i));
				//System.out.println("Datafile: " + dataFile);

				// Initialize the date file
				File f = new File(s + "data.dat");
				dataFile = f.getAbsolutePath();

				if (!(f.length() == 0)) {
					System.out.println("We'll see");
					cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
					cal.setAllowInplaceCreate(true);
				} else {
					cal.getSchedule().loadFrom("data.dat", ContentType.Xml);
					;
					cal.setAllowInplaceCreate(true);
				}

			}
		}
	}

	/**
	 * Show the schedule of the patient that has been clicked on
	 */
	private void patientListValueChanged(javax.swing.event.ListSelectionEvent evt) {

		cal.update();
		// set text on right here
		String s = (String) patientList.getSelectedValue();
		for (int i = 0; i < patientList.getModel().getSize(); i++) {
			if (s.equals(patientList.getModel().getElementAt(i))) {

				System.out.println("Selected value:" + s);
				System.out.println("Element at i:" + patientList.getModel().getElementAt(i));
				System.out.println("Datafile: " + dataFile);

				// Initialize the date file
				File f = new File(s + "data.dat");
				dataFile = f.getAbsolutePath();

				if (!(f.length() == 0)) {
					System.out.println("We'll see");
					cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
					cal.setAllowInplaceCreate(true);
				} else {
					cal.getSchedule().loadFrom("data.dat", ContentType.Xml);
					;
					cal.setAllowInplaceCreate(true);
				}

			}
		}
	}

	/**
	 * Delete the schedule of the name that has been right clicked
	 */
	private void deleteSchedule(JList list) {
		list.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					// Warning message before delete
					if (JOptionPane.showConfirmDialog(null, "Delete?", "Warning", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE) == 0) {
						
						if(list == employeeList) {
							try {
								System.out.println((String) list.getSelectedValue());
								updateFile(list, employeeFile, (String) list.getSelectedValue());
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else if(list == patientList) {
							try {
								System.out.println((String) list.getSelectedValue());
								updateFile(list, patientFile, (String) list.getSelectedValue());
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						
						String fileDelete = list.getSelectedValue() + "data.dat";
						File file = new File(fileDelete);
						file.delete();
						((SortedListModel) list.getModel()).removeElement(list.getSelectedValue());
						
						
					}

				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

		});

	}
	
	/**
	 * Make file to load in employees
	 */
	private void loadEmployeeFile() {
		//File f2 = new File("employees.txt");
		if (!employeeFile.exists()) {
			try {
				employeeFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				e1.getMessage();
				System.out.println("Something went wrong when trying to create the employees.txt file");
			}
		} else {
			String dataFile2 = employeeFile.getAbsolutePath();
			try (BufferedReader reader = new BufferedReader(new FileReader(dataFile2))) {
				//System.out.println("Gonna try to read employees.txt");
				String temp = reader.readLine();
				while (temp != null) {
					//System.out.println("In the while loop");
					Employee employee = new Employee();
					boolean commaRead = false;
					String nameHolder = "";
					for (int i = 0; i < temp.length(); i++) {
						//System.out.println("In the for loop");
						if (temp.charAt(i) != ';' && commaRead == false) {
							nameHolder += temp.charAt(i);
						} else if (temp.charAt(i) == ';') {
							commaRead = true;
							employee.setFirstName(nameHolder);
							nameHolder = "";
						} else if (i != temp.length() - 1) {
							nameHolder += temp.charAt(i);
						} else {
							nameHolder += temp.charAt(i);
							employee.setLastName(nameHolder);
							((SortedListModel) employeeList.getModel())
									.add(employee.getFirstName() + " " + employee.getLastName());

						}
					}
					temp = reader.readLine();
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Make file to load in patients
	 */
	private void loadPatientFile() {
		if (!patientFile.exists()) {
			try {
				patientFile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				e1.getMessage();
				System.out.println("Something went wrong when trying to create the patients.txt file");
			}
		} else {
			String dataFile2 = patientFile.getAbsolutePath();
			try (BufferedReader reader = new BufferedReader(new FileReader(dataFile2))) {
				//System.out.println("Gonna try to read patients.txt");
				String temp = reader.readLine();
				while (temp != null) {
					//System.out.println("In the while loop");
					Patient patient = new Patient();
					// Contact contact = new Contact();
					boolean commaRead = false;
					String nameHolder = "";
					for (int i = 0; i < temp.length(); i++) {
						//System.out.println("In the for loop");
						if (temp.charAt(i) != ';' && commaRead == false) {
							nameHolder += temp.charAt(i);
						} else if (temp.charAt(i) == ';') {
							commaRead = true;
							patient.setFirstName(nameHolder);
							nameHolder = "";
						} else if (i != temp.length() - 1) {
							nameHolder += temp.charAt(i);
						} else {
							nameHolder += temp.charAt(i);
							patient.setLastName(nameHolder);
							((SortedListModel) patientList.getModel())
									.add(patient.getFirstName() + " " + patient.getLastName());

						}
					}
					temp = reader.readLine();
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Make new employees.txt and patients.txt file on delete
	 */
	private void updateFile(JList list, File f, String name) throws IOException{
		File tempFile = new File("temp.txt");
		String lineToRemove = formatName(name);
		System.out.println("line to remove: " + lineToRemove);
		String currentLine;
		
		BufferedReader reader = new BufferedReader(new FileReader(f));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
		
		try {
			while((currentLine = reader.readLine()) != null) {
			    // trim newline when comparing with lineToRemove
			    String trimmedLine = currentLine.trim();
			    if(trimmedLine.equals(lineToRemove)) continue;
			    writer.write(currentLine + System.getProperty("line.separator") + "\n");
			}
		}finally {
			writer.close(); 
			reader.close(); 
		}

		
		
		
		tempFile.renameTo(f);
		
	}
	
	/**
	 * Format string to feed into output file
	 */
	private String formatName(String name) {
		String nameHolder = "";
		boolean spaceRead = false;
		for (int i = 0; i < name.length(); i++) {
			if (!(Character.isWhitespace(name.charAt(i))) && spaceRead == false) {
				nameHolder += name.charAt(i);
			} else if (Character.isWhitespace(name.charAt(i))) {
				spaceRead = true;
				// employee.setFirstName(nameHolder);
				nameHolder += ";";
			} else if (i != name.length() - 1) {
				nameHolder += name.charAt(i);
			} else {
				nameHolder += name.charAt(i);

			}	
		}
		System.out.println("name holder: " + nameHolder);
		return nameHolder;
	}
}
