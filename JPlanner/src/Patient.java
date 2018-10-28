import com.mindfusion.scheduling.Calendar;

public class Patient {
	
	Calendar calendar;
	String name;
	
	public Patient() {
	}
	
	public Patient(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Calendar getCalendar() {
		return calendar;
	}

}
