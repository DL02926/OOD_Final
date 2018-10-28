import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.CalendarAdapter;
import com.mindfusion.scheduling.ItemMouseEvent;
import com.mindfusion.scheduling.model.ContentType;
import com.mindfusion.scheduling.model.Schedule;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JPlanner {

	private JFrame frame;
	private String dataFile;
	private Calendar cal;
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
		frame.setBounds(100, 100, 850, 600);
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
		Employee e1 = new Employee("Austin Couey");
		Employee e2 = new Employee("Marcus Butler");

		/**
		 * This is how you add a new item to a JList
		 * Added element must be a string
		 * Default list model needs to be changed to something that will order the list automatically
		 */
		((SortedListModel)employeeList.getModel()).add(e1.getName());
		((SortedListModel)employeeList.getModel()).add(e2.getName());


		/**
		 * This is how you interact with items in the JList
		 */
		employeeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent evt) {
				employeeListValueChanged(evt);
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
		cal = new Calendar();
		calPanel.add(cal);
		cal.addCalendarListener(new CalendarAdapter() {
			@Override
			public void itemClick(ItemMouseEvent e) {
				if (e.getItem() instanceof Shift)
				{
					cal.resetDrag();
					JOptionPane.showInputDialog("Enter name here");
				}
			}


		});

		cal.setInteractiveItemType(Shift.class);

		Schedule.registerItemClass(Shift.class, "shift", 1);



		// Initialize the date file
		dataFile = new java.io.File("data.dat").getAbsolutePath();

		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				cal.getSchedule().saveTo(dataFile, ContentType.Xml);
			}
			public void windowOpened(WindowEvent e){
				cal.getSchedule().loadFrom(dataFile, ContentType.Xml);
			}
		});



	}

	/**
	 * TODO: patient name still adds when you only type in a first name and the warning box pops up and you press okay
	 */
	private void addPatient() {
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
			}
		}



		if(firstName.getText()!="" && lastName.getText()!="") {

			Patient patient = new Patient(firstName.getText() + " " + lastName.getText());
			((SortedListModel)patientList.getModel()).add(patient.getName());		//Fucking right finally got this shit to work


		}else{ 
			System.out.println("Must enter first and last name of patient.");
		}
	}

	
	/**
	 * TODO: employee name still adds when you only type in a first name and the warning box pops up and you press okay
	 */
	private void addEmployee() {
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
			}
		}



		if(firstName.getText()!="" && lastName.getText()!="") {

			Employee employee = new Employee(firstName.getText() + " " + lastName.getText());
			((SortedListModel)employeeList.getModel()).add(employee.getName());		//Fucking right finally got this shit to work


		}else{ 
			System.out.println("Must enter first and last name of employee.");
		}
	}

	/**
	 * If employee's name is selected in JList, display their calendar
	 * Probably load the calendar in from a data file (employeename.dat)
	 */
	private void employeeListValueChanged(javax.swing.event.ListSelectionEvent evt) {
		//set text on right here
		String s = (String) employeeList.getSelectedValue();
		if (s.equals("Item 1")) {
			//set calPanel = employee calendar here
			System.out.println("1 Changed");
		}
		if (s.equals("Item 2")) {
			System.out.println("2 Changed");
		}
	}


}
