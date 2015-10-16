package spongecell.guardian.notification;

import java.time.LocalDateTime;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

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
	public Object body = new String();
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
	
	public String getJsonEventMessage() throws JsonProcessingException {
		ObjectNode event = new ObjectMapper().createObjectNode();
		event.put ("source", source);
		event.put ("timestamp", dateTime);
		event.put("severity", eventSeverity);
		
		int i = 1;
		if (body instanceof ArrayNode) {
			Iterator<JsonNode> nodes = ((ArrayNode)body).iterator();
			while (nodes.hasNext()) {
				JsonNode node = nodes.next();
				event.put("Row " + i,  new ObjectMapper()
					.writerWithDefaultPrettyPrinter()
					.writeValueAsString(node).replace("\n", "").replace("\"", ""));
				i++;
			}
		}
		return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(event);
	}
}

