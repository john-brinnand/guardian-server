package spongecell.guardian.test;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static spongecell.webhdfs.WebHdfsParams.DEFAULT_PERMISSIONS;
import static spongecell.webhdfs.WebHdfsParams.FILE;
import static spongecell.webhdfs.WebHdfsParams.FILE_STATUS;
import static spongecell.webhdfs.WebHdfsParams.FILE_STATUSES;
import static spongecell.webhdfs.WebHdfsParams.PERMISSION;
import static spongecell.webhdfs.WebHdfsParams.TYPE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.naming.factory.SendMailFactory;
import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import spongecell.guardian.listener.RuleEventListener;
import spongecell.guardian.model.HDFSDirectory;
import spongecell.guardian.notification.SimpleMailClient;
import spongecell.webhdfs.FilePath;
import spongecell.webhdfs.WebHdfsConfiguration;
import spongecell.webhdfs.WebHdfsOps;
import spongecell.webhdfs.WebHdfsWorkFlow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Slf4j
@ContextConfiguration(classes = { HDFSBusinessRulesTest.class, WebHdfsWorkFlow.Builder.class})
@EnableConfigurationProperties ({ WebHdfsConfiguration.class })
public class HDFSBusinessRulesTest extends AbstractTestNGSpringContextTests {
	private final static String BASE_PATH = "src/main/resources";
	private KieSession kieSession;
	@Autowired WebHdfsWorkFlow.Builder webHdfsWorkFlowBuilder;
	@Autowired WebHdfsConfiguration webHdfsConfig;
	
	@BeforeTest
	public void init() {
		String[] rules = { 
			"/spongecell/guardian/rules/core/hdfs-directory.drl", 
			"/spongecell/guardian/rules/core/hdfs-directory-notification.drl"
		}; 
		KieServices kieServices = KieServices.Factory.get();
		KieResources kieResources = kieServices.getResources();
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		KieRepository kieRepository = kieServices.getRepository();

		for (String rule : rules) {
			InputStream ruleIn = getClass().getResourceAsStream(rule);
//					"/spongecell/guardian/rules/core/hdfs-directory.drl");
			Assert.assertNotNull(ruleIn);
			String path = "src/main/resources/spongecell/"
					+ "guardian/rules/core/" + rule;
			kieFileSystem.write(path,
					kieResources.newInputStreamResource(ruleIn, "UTF-8"));
		}
		KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
		kb.buildAll();

		if (kb.getResults().hasMessages(Level.ERROR)) {
			throw new RuntimeException("Build Errors:\n"
					+ kb.getResults().toString());
		}
		KieContainer kieContainer = kieServices.newKieContainer(
			kieRepository.getDefaultReleaseId());

		kieSession = kieContainer.newKieSession();
		kieSession.addEventListener(new RuleEventListener());
		kieSession.addEventListener(new DebugRuleRuntimeEventListener());
	}
	@Test
	public void validateWorkFlowCreateDirFile() throws NoSuchMethodException, 
		SecurityException, UnsupportedEncodingException, URISyntaxException {
		Assert.assertNotNull(webHdfsWorkFlowBuilder);
		
		StringEntity entity = new StringEntity("Greetings earthling!\n");
		
		DateTimeFormatter customDTF = new DateTimeFormatterBuilder()
	        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
	        .appendValue(MONTH_OF_YEAR, 2)
	        
	        .appendValue(DAY_OF_MONTH, 2)
	        .toFormatter();	
		
		FilePath path = new FilePath.Builder()
			.addPathSegment("data")
			.addPathSegment(customDTF.format(LocalDate.now()))
			.build();
		
		String fileName = path.getFile().getPath() + File.separator + webHdfsConfig.getFileName();
		
		WebHdfsWorkFlow workFlow = webHdfsWorkFlowBuilder
			.path(path.getFile().getPath())
			.addEntry("CreateBaseDir", 
				WebHdfsOps.MKDIRS, 
				HttpStatus.OK, 
				path.getFile().getPath())
			.addEntry("SetOwnerBaseDir",
				WebHdfsOps.SETOWNER, 
				HttpStatus.OK, 
				webHdfsConfig.getBaseDir(), 
				webHdfsConfig.getOwner(), 
				webHdfsConfig.getGroup())
			.addEntry("CreateAndWriteToFile", 
				WebHdfsOps.CREATE, 
				HttpStatus.CREATED, 
				entity)
			.addEntry("SetOwnerFile", WebHdfsOps.SETOWNER, 
				HttpStatus.OK, 
				fileName,
				webHdfsConfig.getOwner(), 
				webHdfsConfig.getGroup())
			.build();
		CloseableHttpResponse response = workFlow.execute(); 
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());
	}
	
	@Test(dependsOnMethods="validateWorkFlowCreateDirFile")
	public void validateWorkFlowFileCreateWrite() throws URISyntaxException, IOException {
		Assert.assertNotNull(webHdfsWorkFlowBuilder);
		
		// Override the default file name.
		//********************************
		webHdfsConfig.setFileName("testfile1.txt");
		
		StringEntity entity = new StringEntity("Greetings earthling!\n");
		
		DateTimeFormatter customDTF = new DateTimeFormatterBuilder()
	        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
	        .appendValue(MONTH_OF_YEAR, 2)
	        .appendValue(DAY_OF_MONTH, 2)
	        .toFormatter();	
		
		FilePath path = new FilePath.Builder()
			.addPathSegment("data")
			.addPathSegment(customDTF.format(LocalDate.now()))
			.build();
		
		String fileName = path.getFile().getPath() + File.separator + "testfile1.txt";
		
		WebHdfsWorkFlow workFlow = webHdfsWorkFlowBuilder
			.path(path.getFile().getPath())
			.addEntry("CreateAndWriteToFile", 
				WebHdfsOps.CREATE, 
				HttpStatus.CREATED, 
				entity)
			.addEntry("SetOwnerFile", WebHdfsOps.SETOWNER, 
				HttpStatus.OK, 
				fileName,
				webHdfsConfig.getOwner(), 
				webHdfsConfig.getGroup())
			.build();
		CloseableHttpResponse response = workFlow.execute(); 
		Assert.assertNotNull(response);
		Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.OK.value());	
	}
	
	@Test(dependsOnMethods="validateWorkFlowFileCreateWrite")
	public void validateWebHdfsListStatus() throws URISyntaxException, IOException {
		Assert.assertNotNull(webHdfsWorkFlowBuilder);
		
		// Override the default file name.
		//********************************
		DateTimeFormatter customDTF = new DateTimeFormatterBuilder()
	        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
	        .appendValue(MONTH_OF_YEAR, 2)
	        .appendValue(DAY_OF_MONTH, 2)
	        .toFormatter();	
		
		FilePath path = new FilePath.Builder()
			.addPathSegment("data")
			.addPathSegment(customDTF.format(LocalDate.now()))
			.build();
		
		WebHdfsWorkFlow workFlow = webHdfsWorkFlowBuilder
			.path(path.getFile().getPath())
			.addEntry("ListDirectoryStatus", 
				WebHdfsOps.LISTSTATUS, 
				HttpStatus.OK, 
				webHdfsConfig.getBaseDir())
			.build();
		CloseableHttpResponse response = workFlow.execute(); 
		Assert.assertNotNull(response);
		Assert.assertEquals(HttpStatus.OK.value(), response.getStatusLine().getStatusCode());			
		ObjectNode dirStatus = new ObjectMapper().readValue(
			EntityUtils.toString(response.getEntity()), 
			new TypeReference<ObjectNode>() {
		});
		log.info("Directory status is: {} ", new ObjectMapper()
			.writerWithDefaultPrettyPrinter()
			.writeValueAsString(dirStatus));
		
		ArrayNode fileStatus  = new ObjectMapper().readValue(dirStatus
			.get(FILE_STATUSES)
			.get(FILE_STATUS).toString(),
			new TypeReference<ArrayNode>() { 
		});
		for (int i = 0; i < fileStatus.size(); i++) {
			JsonNode fileStatusNode = fileStatus.get(i);
			Assert.assertEquals(fileStatusNode.get(TYPE).asText(), FILE);
			Assert.assertEquals(fileStatusNode.get(PERMISSION).asText(), DEFAULT_PERMISSIONS);
		}
		//*******************************************
		// Test the rule.
		//*******************************************
		HDFSDirectory hdfsDir = new HDFSDirectory();
		hdfsDir.setNumChildren(fileStatus.size());
		hdfsDir.setOwner("root");
		
		SimpleMailClient smc = new SimpleMailClient();
		
		Object[] facts = { hdfsDir, smc };
		log.info ("Running the HDFSDirectory children's rule.");
		
		for (Object fact : facts) {
			kieSession.insert(fact);
		}
		int numRules = kieSession.fireAllRules();
		Assert.assertEquals(4, numRules);
	}
}	