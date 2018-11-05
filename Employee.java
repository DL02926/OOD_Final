import java.util.ArrayList;

import com.mindfusion.scheduling.model.Contact;

public class Employee extends Contact{
	private String name;
	private float hours;
	public ArrayList<Patient> patients;
	
	
	public Employee() {
		
		
		//dataFile = new java.io.File("data.dat").getAbsolutePath();
	}
	
	public Employee(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public float getHours() {
		return hours;
	}
	
	public void setHours(float hours) {
		this.hours = hours;
	}
	
	public ArrayList<Patient> getPatients(){
		return patients;
	}
	
	public void addPatient(Patient patient) {
		patients.add(patient);
	}
	
	
	

}
