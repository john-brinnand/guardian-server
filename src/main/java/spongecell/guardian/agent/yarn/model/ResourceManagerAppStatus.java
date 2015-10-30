package spongecell.guardian.agent.yarn.model;

import static spongecell.guardian.agent.yarn.ResourceManagerAppMonitorConfiguration.*;

import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author jbrinnand
 */
@Slf4j
@Getter @Setter
public class ResourceManagerAppStatus {
	private JsonNode appStatus;
	private boolean active = false;
	
	public static ResourceManagerAppStatus instance() {
		return new ResourceManagerAppStatus();
	}
	
	public String getState () {
		return appStatus.get(APP).get(STATE).asText();
	}
	
	public String getFinalStatus () {
		return appStatus.get(APP).get(FINAL_STATUS).asText();
	}	
	
	public String getFileStatus() {
		StringBuffer sbuf = new StringBuffer();
		Iterator<JsonNode> nodes = appStatus.iterator();
		while (nodes.hasNext()) {
			JsonNode node = nodes.next();
			sbuf.append(node.toString() + "\n");
		}
		log.info("*********************** \n" + sbuf.toString());
		return sbuf.toString();
	} 
}
