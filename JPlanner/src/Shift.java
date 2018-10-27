


import javax.lang.model.element.Element;

import com.mindfusion.common.DateTime;
import com.mindfusion.scheduling.model.Appointment;
import com.mindfusion.scheduling.model.Contact;
import com.mindfusion.scheduling.model.Item;
import com.mindfusion.scheduling.model.Location;
import com.mindfusion.scheduling.model.Reminder;
import com.mindfusion.scheduling.model.Resource;
import com.mindfusion.scheduling.model.ResourceList;
import com.mindfusion.scheduling.model.Style;
import com.mindfusion.scheduling.model.Task;
import com.mindfusion.scheduling.model.XmlSerializationContext;

public class Shift extends Appointment{
	private boolean _kept;
	
	public Shift()
    {
        _kept = true;
    }

    public boolean getKept()
    {
        return _kept;
    }

    public void setKept(boolean value)
    {
        _kept = value;
    }

    

    
	

}
