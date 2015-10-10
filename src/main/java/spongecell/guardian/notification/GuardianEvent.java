package spongecell.guardian.notification;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class GuardianEvent {
	public String source;
	public String dateTime ;
	public String managedObject;
	
	public static enum severity {
		INFORMATIONAL,
		MINOR,
		MAJOR,
		CRITICAL
	};
	public String absolutePath;
	public String message = new String();
	public String body = new String();
	public String eventSeverity;
	private boolean valid = true;
	
	public GuardianEvent () {
		dateTime = LocalDateTime.now().toString();
	}
	
	public String getMessage() {
		message = String.format("%s : %s,\n%s : %s\n%s : %s\n%s\n%s\n%s", 
			"ManagedObject", managedObject, 
			"Source", source, 
			"Severity", eventSeverity, 
			"*****************************************************************", 
			 body, 
			"*****************************************************************"); 
		return message;
	}
}

