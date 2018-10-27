import java.util.ArrayList;

public class Employee {
	private String name;
	private float hours;
	public ArrayList<Patient> patients;
	
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
