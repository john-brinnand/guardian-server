package spongecell.guardian.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
	private String targetDir;
	private String HDFS = "HDFS";
	private String COLON = ":";

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

	public void setTargetDir(String targetDir) {
		this.targetDir = HDFS + COLON + targetDir;
	}	
	
	public Workbook getExcelFileStatusWorkBook() throws IOException {
		Workbook workBook = new HSSFWorkbook();
		
		CreationHelper createHelper = workBook.getCreationHelper();
	    Sheet sheet = workBook.createSheet("HDFS File System Report");
		
	    short i = 0;
		Iterator<JsonNode> nodes = fileStatus.iterator();
		while (nodes.hasNext()) {
			Row row = sheet.createRow(i);
			JsonNode node = nodes.next();
			Iterator<JsonNode> values = node.elements(); 
			int j = 0;
			while (values.hasNext()) {
				JsonNode value = values.next();
				row.createCell(j).setCellValue(createHelper.createRichTextString(value.toString()));
				j++;
			}
			i++;
		}
		log.info("*********************** \n" + workBook.toString());
	
		return workBook;
	} 
}
