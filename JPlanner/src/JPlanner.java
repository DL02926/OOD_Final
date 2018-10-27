import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.mindfusion.scheduling.Calendar;
import com.mindfusion.scheduling.CalendarAdapter;
import com.mindfusion.scheduling.ItemMouseEvent;
import com.mindfusion.scheduling.model.ContentType;
import com.mindfusion.scheduling.model.ItemEvent;
import com.mindfusion.scheduling.model.Schedule;
import com.mindfusion.scheduling.DateEvent;
import com.mindfusion.scheduling.ResourceDateEvent;

public class JPlanner {

	private JFrame frame;
	private JTable pTable;
	private JTable eTable;
	private String dataFile;
	private Calendar cal;

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
				JPanel panel = new JPanel();
				frame.getContentPane().add(panel, BorderLayout.NORTH);
				panel.setLayout(new GridLayout(0, 3, 0, 0));
				
				//Add new employee button
				JButton btnAddEmployee = new JButton("Add Employee");
				btnAddEmployee.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
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
							//JOptionPane.showMessageDialog(null, "Must enter a first and last name", "Warning", JOptionPane.WARNING_MESSAGE);
								JDialog d = addEmpPane.createDialog(null, "Warning");
								d.setVisible(true);
							}
						}
						
						
						
						if(firstName.getText()!="" && lastName.getText()!="") {
							
							Employee returnedEmployee = new Employee();
							returnedEmployee.setName(firstName.getText() + " " + lastName.getText());
							System.out.println(returnedEmployee.getName());
						}else{ 
							System.out.println("Must enter first and last name of employee.");
						}
						}
					
				});
				panel.add(btnAddEmployee);
				
				//Add new patient button
				JButton btnAddPatient = new JButton("Add Patient");
				btnAddPatient.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JTextField firstName = new JTextField();
						JTextField lastName = new JTextField();
						
						Object[] fields = {
								"First Name: ", firstName,
								"Last Name: ", lastName
						};
						JOptionPane addEmpPane = new JOptionPane(fields, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
						
						
						if(JOptionPane.showConfirmDialog(null, fields, "Add Patient", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)==0) {
							System.out.println("We're in");
							if(firstName.getText().equals("") || lastName.getText().equals("")) {
							System.out.println("We're still in");
							//JOptionPane.showMessageDialog(null, "Must enter a first and last name", "Warning", JOptionPane.WARNING_MESSAGE);
								JDialog d = addEmpPane.createDialog(null, "Warning");
								d.setVisible(true);
								d.dispose();
							}
						}
						
						
						
						if(firstName.getText()!="" && lastName.getText()!="") {
							
							Employee returnedEmployee = new Employee();
							returnedEmployee.setName(firstName.getText() + " " + lastName.getText());
							System.out.println(returnedEmployee.getName());
						}else{ 
							System.out.println("Must enter first and last name of employee.");
						}
					}	
				});
				panel.add(btnAddPatient);
				
				//Drop down to choose calendar view
				JComboBox viewTypeCombo = new JComboBox();
				viewTypeCombo.setToolTipText("View Type");
				panel.add(viewTypeCombo);
				
				//Panel for patients to be listed on left side of window
				JPanel patientPanel = new JPanel();
				frame.getContentPane().add(patientPanel, BorderLayout.WEST);
				GridBagLayout gbl_patientPanel = new GridBagLayout();
				gbl_patientPanel.columnWidths = new int[]{50, 1, 0};
				gbl_patientPanel.rowHeights = new int[]{16, 0, 0};
				gbl_patientPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				gbl_patientPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				patientPanel.setLayout(gbl_patientPanel);
				
				//Display name of what we are displaying
				JLabel lblPatients = new JLabel("Patients");
				GridBagConstraints gbc_lblPatients = new GridBagConstraints();
				gbc_lblPatients.anchor = GridBagConstraints.NORTHWEST;
				gbc_lblPatients.insets = new Insets(0, 0, 5, 5);
				gbc_lblPatients.gridx = 0;
				gbc_lblPatients.gridy = 0;
				patientPanel.add(lblPatients, gbc_lblPatients);
				
				//Add new clickable list of patients
				JList patientList = new JList();
				GridBagConstraints gbc_patientList = new GridBagConstraints();
				gbc_patientList.insets = new Insets(0, 0, 5, 0);
				gbc_patientList.anchor = GridBagConstraints.WEST;
				gbc_patientList.gridx = 1;
				gbc_patientList.gridy = 0;
				patientPanel.add(patientList, gbc_patientList);
				
				pTable = new JTable(10, 1);
				pTable.setGridColor(Color.BLACK);
				GridBagConstraints gbc_pTable = new GridBagConstraints();
				gbc_pTable.insets = new Insets(0, 0, 0, 5);
				gbc_pTable.fill = GridBagConstraints.VERTICAL;
				gbc_pTable.gridx = 0;
				gbc_pTable.gridy = 1;
				patientPanel.add(pTable, gbc_pTable);
				
				
				//set names
				pTable.setValueAt("Carr", 0, 0);
				pTable.setValueAt("Smith", 1, 0);
				pTable.setValueAt("Johnson", 2, 0);
				pTable.setValueAt("Arquette", 3, 0);
				pTable.setValueAt("Aniston", 4, 0);
				pTable.setValueAt("Bieber", 5, 0);
				pTable.setValueAt("Patel", 6, 0);
				pTable.setValueAt("Long", 7, 0);
				pTable.setValueAt("Lloyd", 8, 0);
				pTable.setValueAt("Butler", 9, 0);
				
				//Panel for employees to be listed on right side of panel
				JPanel employeePanel = new JPanel();
				frame.getContentPane().add(employeePanel, BorderLayout.EAST);
				GridBagLayout gbl_employeePanel = new GridBagLayout();
				gbl_employeePanel.columnWidths = new int[]{67, 1, 0};
				gbl_employeePanel.rowHeights = new int[]{16, 0, 0};
				gbl_employeePanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
				gbl_employeePanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
				employeePanel.setLayout(gbl_employeePanel);
				employeePanel.setSize(200, 400);
				
				//Display name of what we are displaying
				JLabel lblEmployees = new JLabel("Employees");
				GridBagConstraints gbc_lblEmployees = new GridBagConstraints();
				gbc_lblEmployees.anchor = GridBagConstraints.NORTHWEST;
				gbc_lblEmployees.insets = new Insets(0, 0, 5, 5);
				gbc_lblEmployees.gridx = 0;
				gbc_lblEmployees.gridy = 0;
				employeePanel.add(lblEmployees, gbc_lblEmployees);
				
				//Add a clickable list of employees
				JList employeeList = new JList();
				GridBagConstraints gbc_employeeList = new GridBagConstraints();
				gbc_employeeList.insets = new Insets(0, 0, 5, 0);
				gbc_employeeList.anchor = GridBagConstraints.WEST;
				gbc_employeeList.gridx = 1;
				gbc_employeeList.gridy = 0;
				employeePanel.add(employeeList, gbc_employeeList);
				
				eTable = new JTable(10, 1);
				eTable.setGridColor(Color.BLACK);
				GridBagConstraints gbc_eTable = new GridBagConstraints();
				gbc_eTable.insets = new Insets(0, 0, 0, 5);
				gbc_eTable.fill = GridBagConstraints.BOTH;
				gbc_eTable.gridx = 0;
				gbc_eTable.gridy = 1;
				employeePanel.add(eTable, gbc_eTable);
				
				
				//setEmployees
				eTable.setValueAt("Karen Filippelli", 0, 0);
				eTable.setValueAt("Phyllis Vance", 1, 0);
				eTable.setValueAt("Oscar Martinez", 2, 0);
				eTable.setValueAt("Ryan Howard", 3, 0);
				eTable.setValueAt("Jim Halpert", 4, 0);
				eTable.setValueAt("Andy Bernard", 5, 0);
				eTable.setValueAt("Dwight K. Schrute", 6, 0);
				eTable.setValueAt("Robert California", 7, 0);
				eTable.setValueAt("Gabe Lewis", 8, 0);
				eTable.setValueAt("Michael Scott", 9, 0);
				
				
				//Make Calendar panel
				JPanel calPanel = new JPanel();
				frame.getContentPane().add(calPanel, BorderLayout.CENTER);
				calPanel.setLayout(new GridLayout(1, 0, 0, 0));
				



				
				//Display calendar
				cal = new Calendar();
				cal.addCalendarListener(new CalendarAdapter() {
					@Override
					public void itemClick(ItemMouseEvent e) {
						if (e.getItem() instanceof Shift)
						{
						    cal.resetDrag();
						    JOptionPane.showInputDialog("Enter name here");/*.showMessageDialog(cal, "This is our item.", null, 0);*/
						}
					}
					
					/*
					@Override
					public void dateClick(ResourceDateEvent e) 
					{
						cal.resetDrag();
						JOptionPane.showMessageDialog(cal, e.getDate(), "Date", 0);
					}
					*/
				});
				calPanel.add(cal, BorderLayout.CENTER);
				
				//cal.setInteractiveItemType(Shift.class);
				
				//Schedule.registerItemClass(Shift.class, "shift", 1);
				
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

}
