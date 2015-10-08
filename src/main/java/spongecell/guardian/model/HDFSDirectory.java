package spongecell.guardian.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HDFSDirectory {
	private String owner;
	private String pathSuffix;
	private String type;
	private int numChildren;
	private String statusCheck; 
	public static final String FILE_CHECK_SUCCESS =  "success";
	public static final String FILE_CHECK_FAIL =  "fail";
	public static final String FILE_CHECK_UNKNOWN =  "unknown";
	public boolean valid;
	
	public HDFSDirectory() { 
		statusCheck = FILE_CHECK_UNKNOWN;
		valid = Boolean.TRUE;
	}
}
