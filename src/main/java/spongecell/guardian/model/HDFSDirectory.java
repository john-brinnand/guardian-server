package spongecell.guardian.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class HDFSDirectory {
	private String owner;
	private String pathSuffix;
	private String type;
	private int numChildren;
	
	public HDFSDirectory() {
		
	}
}
