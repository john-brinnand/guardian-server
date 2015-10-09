package spongecell.guardian.model;

import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Slf4j
@Getter @Setter
public class HDFSDirectory {
	private String owner;
	private String pathSuffix;
	private int numChildren;
	private String statusCheck; 
	private Long accessTime;
	private Long blockSize;
	private int childrenNum;
	private int fileId; 
	private String group; 
	private int length; 
	private Long modificationTime;
	private String permission;
	private int replication;
	private int storagePolicy;
	private String type;
	private ArrayNode fileStatus;

	public static final String FILE_CHECK_SUCCESS =  "success";
	public static final String FILE_CHECK_FAIL =  "fail";
	public static final String FILE_CHECK_UNKNOWN =  "unknown";
	public boolean valid;
	
	public HDFSDirectory() { 
		statusCheck = FILE_CHECK_UNKNOWN;
		valid = Boolean.TRUE;
	}
	
	public String getFileStatus() {
		StringBuffer sbuf = new StringBuffer();
		Iterator<JsonNode> nodes = fileStatus.iterator();
		while (nodes.hasNext()) {
			JsonNode node = nodes.next();
			sbuf.append(node.toString() + "\n");
		}
		log.info("*********************** \n" + sbuf.toString());
		return sbuf.toString();
	}
}
