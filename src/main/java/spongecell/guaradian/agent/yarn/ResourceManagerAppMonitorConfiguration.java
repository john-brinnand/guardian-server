package spongecell.guaradian.agent.yarn;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jbrinnand
 */
@Getter @Setter
@ConfigurationProperties(prefix ="app.monitor")
public class ResourceManagerAppMonitorConfiguration {
	public static enum RunStates {
		UNKNOWN("UNKNOWN"),
		UNDEFINED("UNDEFINED"),
		RUNNING("RUNNING"),
		SUCCEEDED("SUCCEEDED"),
		FINISHED("FINISHED");
		private RunStates (String states) { }
	}	
	public static final String STATES = "states";
	public static final String STATE = "state";
	public static final String APP = "app";
	public static final String FINAL_STATUS = "finalStatus";
	
	/**
	 * http://hadoop-production-resourcemanager.spongecell.net:8088/
	 * ws/v1/cluster/apps?states=running"
	 */
	public String scheme = "http";
	public String host = "hadoop-production-resourcemanager.spongecell.net";
	public int port = 8088;
	public String cluster = "ws/v1/cluster";
	public String endpoint = "apps";
}
