import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

	private JFrame frame, frame2;
	private String dataFile;
	private Calendar cal = new Calendar();
	private Calendar cal2;
	private Contact c;
	Schedule s = new Schedule();
	JList<String> employeeList = new JList<String>(new SortedListModel());
	JList<String> patientList = new JList<String>(new SortedListModel());
	JPanel employeePanel, patientPanel;
	ArrayList<Employee> emp = new ArrayList<Employee>();	//not sure if this is needed since we have the JList


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
		//Main frame to put everything in
		frame = new JFrame();
		frame.setBounds(100, 100, 1300, 900);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Panel for top buttons and combo box
		JPanel buttonPanel = new JPanel();
		frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
		buttonPanel.setLayout(new GridLayout(0, 3, 0, 0));

		//Add new employee button
		JButton btnAddEmployee = new JButton("Add Employee");
		btnAddEmployee.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEmployee();

			}

		});
		buttonPanel.add(btnAddEmployee);

		//Add new patient button
		JButton btnAddPatient = new JButton("Add Patient");
		btnAddPatient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addPatient();
			}
		});
		buttonPanel.add(btnAddPatient);

		//Drop down to choose calendar view
		JComboBox viewTypeCombo = new JComboBox();
		viewTypeCombo.setToolTipText("View Type");
		buttonPanel.add(viewTypeCombo);





		//Panel for employees to be listed on right side of panel	
		employeePanel = new JPanel();
		frame.getContentPane().add(employeePanel, BorderLayout.EAST);
		employeePanel.setLayout(new BorderLayout(0, 0));

		JLabel lblEmployee = new JLabel("Employee");
		employeePanel.add(lblEmployee, BorderLayout.NORTH);


		employeePanel.add(employeeList, BorderLayout.CENTER);


		/**
		 * Tester employees
		 */
		Contact e1 = new Contact();
		e1.setFirstName("Austin");
		e1.setLastName("Couey");
		
		Contact e2 = new Contact();
		e2.setFirstName("Marcus");
		e2.setLastName("Butler");
		
		//cal.getSchedule().getContacts().add(e1);
		
		
		//cal.getContacts().add(cal.getSchedule().getContacts().get(0));
		
		
		
		
		
		

		/**
		 * This is how you add a new item to a JList
		 * Added element must be a string
		 * Default list model needs to be changed to something that will order the list automatically
		 */
		((SortedListModel)employeeList.getModel()).add(e1.getFirstName() + " " + e1.getLastName());
		((SortedListModel)employeeList.getModel()).add(e2.getFirstName() + " " + e2.getLastName());


		/**
		 * This is how you interact with items in the JList
		 */
		employeeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				employeeListValueChanged(evt);
			}

			public void mousePressed(MouseEvent e)
	        {
	            System.out.println(e);
	            if ( SwingUtilities.isRightMouseButton(e) )
	            {
	                System.out.println("Row: " + getRow(e.getPoint()));
	                employeeList.setSelectedIndex(getRow(e.getPoint()));
	            }
	        }

		});


		//Panel for patients to be listed on left side of panel
		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel, BorderLayout.WEST);
		patientPanel.setLayout(new BorderLayout(0, 0));

		JLabel lblPatient = new JLabel("Patients");
		patientPanel.add(lblPatient, BorderLayout.NORTH);

		
		patientPanel.add(patientList, BorderLayout.CENTER);




		//Make Calendar panel
		JPanel calPanel = new JPanel();
		frame.getContentPane().add(calPanel, BorderLayout.CENTER);
		calPanel.setLayout(new GridLayout(1, 0, 0, 0));


		//Display calendar
		//cal = new Calendar();
		cal.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				//This currently de-selects the current date only if it is selected and you press delete
				Selection i = cal.getSelection();
				Schedule s = cal.getSchedule();
				if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					i.remove(DateTime.today());
				}
			}
		});
		calPanel.add(cal);
		cal.setCurrentView(com.mindfusion.scheduling.CalendarView.SingleMonth);
		cal.setAllowInplaceCreate(false);
		
		//cal.setSchedule(s);
		cal.addCalendarListener(new CalendarAdapter() {
			
			@Override
			public void itemClick(ItemMouseEvent e) {

				/**
				 * Right click to delete
				 */
				if(e.getButton()==MouseEvent.BUTTON3) {
					//Warning message before delete
					if(JOptionPane.showConfirmDialog(null, "Delete?", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)==0) {
						Item i = e.getItem();
						cal.getSchedule().getItems().remove(i);		//This is how you delete an item 
						
						
						
					}
				}
				/**
				 * Do something if the item is clicked on
				 * Maybe show an edit box?
				 */
				else if (e.getButton()==MouseEvent.BUTTON1) {
					if (e.getItem() instanceof Shift)
					{
						//cal.resetDrag();
						//JOptionPane.showInputDialog("Enter name here");
						
						System.out.println(e.getItem().getHeaderText());

					}
				}
			}

			@Override
			public void dateClick(ResourceDateEvent e) {
				if(e.getClicks() == 2) {
					
					//show popup window to make new appt
					JTextField employeeName = new JTextField();
					JTextField startTime = new JTextField();
					JTextField endTime = new JTextField();

					Object[] fields = {
							"Employee Name: ", employeeName,
							"Start time: ", startTime,
							"End time:", endTime
					};
					JOptionPane addEmpPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					JDialog d = addEmpPane.createDialog(null, "Add New");
					d.setVisible(true);
					
					Appointment a = new Appointment();
					a.setHeaderText(employeeName + " " + startTime + "-" + endTime);
					
					/**
					 * Here I need to take in the date that is clicked on then figure out a way to set the time that is typed in above
					 * Maybe use a drop down box of all times
					 */
					//a.setStartTime(DateTime);
					

					
					/**
					 * Appointment class tester
					 * This makes a new "Meet George" appointment show up when you double click a date
					 */
					Appointment app = new Appointment();
					app.setHeaderText("Meet George");
					app.setDescriptionText("This is a sample appointment");
					//app.setStartTime(new DateTime(2018, 11, 1, 14, 0, 0));
					//app.setEndTime(new DateTime(2018, 11, 1, 16, 30, 0));
					
					app.setStartTime(e.getDate());
					app.setEndTime(e.getDate());
					

					cal.getSchedule().getItems().add(app);
					cal.update();
					cal.resetDrag();
				}
			}
		});
		

		

		cal.setInteractiveItemType(Shift.class);

		Schedule.registerItemClass(Shift.class, "shift", 1);


		
		
		// Initialize the date file
		//dataFile = new java.io.File("data.dat").getAbsolutePath()

		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cal.getSchedule().saveTo(dataFile, ContentType.Xml);
			}
			public void windowOpened(WindowEvent e){
				File f = new File("data.dat");
				dataFile = f.getAbsolutePath();
				if(!f.exists()) {
					cal.getSchedule();
				}else {
					System.out.println("Already exists");
					cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
				}
				File f2 = new File("employees.txt");
				if(!f2.exists()) {
					try {
						f2.createNewFile();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						e1.getMessage();
						System.out.println("Something went wrong when trying to create the employees.txt file");
					}
				}else {
					String dataFile2 = f2.getAbsolutePath();
					try(BufferedReader reader = new BufferedReader(new FileReader(dataFile2))) {
						System.out.println("Gonna try to read employees.txt");
						int counter = 1;
						String temp = reader.readLine();
						while(temp != null) {
							System.out.println("In the while loop");
							Employee employee = new Employee();
							Contact contact = new Contact();
							boolean commaRead = false;
							String nameHolder = "";
							for(int i = 0; i < temp.length(); i++) {
								System.out.println("In the for loop");
								if(temp.charAt(i) != ';' && commaRead == false) {
									nameHolder += temp.charAt(i);
								}else if(temp.charAt(i) == ';') {
									commaRead = true;
									employee.setFirstName(nameHolder);
									nameHolder = "";
								}else if(i != temp.length() - 1) {
									nameHolder += temp.charAt(i);
								}else {
									nameHolder += temp.charAt(i);
									employee.setLastName(nameHolder);
									((SortedListModel)employeeList.getModel()).add(employee.getFirstName() + " " + employee.getLastName());
									emp.add(employee);
									
								}
							}
							temp = reader.readLine();
						}
					}catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				}
				
			}
		});
		 


	}

	/**
	 * TODO: patient name still adds when you only type in a first name and the warning box pops up and you press okay
	 */
	private void addPatient() {
		boolean canAdd = true;
		JTextField firstName = new JTextField();
		JTextField lastName = new JTextField();

		Object[] fields = {
				"First Name: ", firstName,
				"Last Name: ", lastName
		};
		JOptionPane addPatPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		;
		if(JOptionPane.showConfirmDialog(null, fields, "Add Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)==0) {
			System.out.println("We're in");
			if(firstName.getText().equals("") || lastName.getText().equals("")) {
				System.out.println("We're still in");

				JDialog d = addPatPane.createDialog(null, "Warning");
				d.setVisible(true);
				canAdd = false;
			}
			if(firstName.getText()!="" && lastName.getText()!="" && canAdd == true) {
				
				c = new Contact();
				c.setFirstName(firstName.getText());
				c.setLastName(lastName.getText());
				((SortedListModel)patientList.getModel()).add(c.getFirstName() + " " + c.getLastName());

				//Patient patient = new Patient(firstName.getText() + " " + lastName.getText());
				//((SortedListModel)patientList.getModel()).add(patient.getName());		//Fucking right finally got this shit to work


			}else{ 
				System.out.println("Must enter first and last name of patient.");
				
			}
		}



		
	}

	
	/**
	 * TODO: employee name still adds when you only type in a first name and the warning box pops up and you press okay
	 */
	private void addEmployee() {
		boolean canAdd = true;
		JTextField firstName = new JTextField();
		JTextField lastName = new JTextField();

		Object[] fields = {
				"First Name: ", firstName,
				"Last Name: ", lastName
		};
		JOptionPane addEmpPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		;
		if(JOptionPane.showConfirmDialog(null, fields, "Add Employee", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)==0) {
			System.out.println("We're in");
			if(firstName.getText().equals("") || lastName.getText().equals("")) {
				System.out.println("We're still in");
				JDialog d = addEmpPane.createDialog(null, "Warning");
				d.setVisible(true);
				canAdd = false;
			}
			if(firstName.getText()!="" && lastName.getText()!="" && canAdd == true) {
				
				c = new Contact();
				c.setFirstName(firstName.getText());
				c.setLastName(lastName.getText());
				Employee employee = new Employee();
				employee.setFirstName(firstName.getText());
				employee.setLastName(lastName.getText());
				
				((SortedListModel)employeeList.getModel()).add(c.getFirstName() + " " + c.getLastName());
				emp.add(employee);
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter("employees.txt", true));
					writer.write(c.getFirstName() + ";" + c.getLastName() + "\n");
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				//Employee employee = new Employee(firstName.getText() + " " + lastName.getText());
				//((SortedListModel)employeeList.getModel()).add(employee.getName());		//Fucking right finally got this shit to work


			}else{ 
				System.out.println("Must enter first and last name of employee.");
			}
		}



		
	}

	/**
	 * If employee's name is selected in JList, display their calendar
	 * Probably load the calendar in from a data file (employeename.dat)
	 */
	private void employeeListValueChanged(javax.swing.event.ListSelectionEvent evt) {
		
		
		
		//set text on right here
		String s = (String) employeeList.getSelectedValue();
		for(int i=0; i<employeeList.getModel().getSize(); i++) {
			if(s.equals(employeeList.getModel().getElementAt(i))) {

				// Initialize the date file
				dataFile = new java.io.File(s + "data.dat").getAbsolutePath();
				
				cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
				cal.setAllowInplaceCreate(true);

				/*
				frame2.addWindowListener(new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						cal2.getSchedule().saveTo(dataFile, ContentType.Xml);
					}
					public void windowOpened(WindowEvent e){
						cal2.getSchedule().loadFrom(dataFile, ContentType.Xml);
					}
				});
				*/
			}
		}
	}

	
	private int getRow(Point point)
    {
       return employeeList.locationToIndex(point);
    }
	
	


}
