package spongecell.guardian.test;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.kie.api.builder.KieRepository;
import org.kie.api.builder.KieScannerFactoryService;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import spongecell.guardian.model.Person;

@Slf4j
@ContextConfiguration(classes = { KIESpringBusinessRulesTest.class, KieSessionHandler.class })
@EnableConfigurationProperties ({ })
public class KIESpringBusinessRulesTest extends AbstractTestNGSpringContextTests {
	private @Autowired KieSessionHandler kieSessionHandler;
	private static final String PATH = "testPath";
	private static final String BASE_PATH = "src/main/resources";
	private static final String RULES_PATH = "spongecell/guardian/rules/core";

	@Test
	public void validateKIEConfiguration () {
		String[] rules = kieSessionHandler.getRules();
		Assert.assertNotNull(rules);
		
		String path = kieSessionHandler.getPath();
		Assert.assertEquals(PATH, path);
		
		String basePath = kieSessionHandler.getBasePath();
		Assert.assertEquals(BASE_PATH, basePath);
		
		String rulesPath = kieSessionHandler.getRulesPath();
		Assert.assertEquals(RULES_PATH, rulesPath);
		
		Assert.assertNotNull(kieSessionHandler.getKieSession());
		Assert.assertNotNull(kieSessionHandler.getKieRepository());
		
		KieRepository kieRepository = kieSessionHandler.getKieRepository();
		Assert.assertNotNull(kieRepository.getKieModule(kieRepository
				.getDefaultReleaseId()));
		log.info("Module is: {} ",
			kieRepository.getKieModule(kieRepository.getDefaultReleaseId()));	
	}
	@Test
	public void validateKieSessionHandler() {
		Person person = new Person("Joe Test", 22);
		List<Person> list = new ArrayList<Person>();
		
		KieSession kieSession = kieSessionHandler.getKieSession();
		kieSession.insert(person);
		kieSession.setGlobal("list", list);	
		kieSession.fireAllRules();			
		Assert.assertEquals(1, list.size());	
		
		KieRepository kieRepository = kieSessionHandler.getKieRepository();
		log.info("Module is: {} ", kieRepository
			.getKieModule(kieRepository.getDefaultReleaseId()));	
	}
	
	@Test
	public void validateKModuleFileSystem () {
		Person person = new Person("Joe Test", 22);
		List<Person> list = new ArrayList<Person>();
		
		KieSession kieSession = kieSessionHandler.buildKIESessionKModuleFile();
		Assert.assertNotNull(kieSession);
		kieSession.setGlobal("list", list);
		kieSession.insert(person);
		kieSession.fireAllRules();
		Assert.assertEquals(1, list.size());
	}
	
	@Test
	public void validatecKModuleMemoryFileSystem () {
		KieSession kieSession = kieSessionHandler
				.buildKIESessionKModuleMemoryFileSystem();
		Assert.assertNotNull(kieSession);
		validateSession(kieSession);
	}	
	
	@Test
	public void validateRepositorySessionAccess() {
		// This creates a module with the id: 
		// spongecell:core:0.0.1-SNAPSHOT to the repository
		//*************************************************
		KieSession kieSession = kieSessionHandler.buildKIESessionKModuleMemoryFileSystem();
		validateSession(kieSession);
		
		// Now get the module from the repository and validate
		// that the rules fire correctly.
		//******************************************************
		KieSession kieSession1 = kieSessionHandler.getRepositorySession(
				"spongecell", "core", "0.0.1-SNAPSHOT", "KSession1");
		validateSession(kieSession1);
	}
	
	private void validateSession(KieSession kieSession) {
		Person person = new Person("Joe Test", 22);
		List<Person> list = new ArrayList<Person>();
		list.clear();
		kieSession.setGlobal("list", list);
		kieSession.insert(person);
		kieSession.fireAllRules();
		Assert.assertEquals(1, list.size());		
	}
	
	@Test
	public void validateKieScanner () {
		// This creates a module with the id: 
		// spongecell:core:0.0.1-SNAPSHOT to the repository
		//*************************************************
		KieSession kieSession = kieSessionHandler.buildKIESessionKModuleMemoryFileSystem();
		validateSession(kieSession);
		kieSessionHandler.scanKieRepository("spongecell", "core",
				"0.0.1-SNAPSHOT", "KSession1");
		log.info("Pause");
	}	

	@Test
	public void validateReleaseId () {
		// This creates a module with the id: 
		// spongecell:core:0.0.1-SNAPSHOT to the repository
		//*************************************************
		KieSession kieSession = kieSessionHandler.buildKieSessionReleaseId(
				"spongecell", "testCore", "0.0.1-SNAPSHOT");
		validateSession(kieSession);
		
		// Now get the module from the repository and validate
		// that the rules fire correctly.
		//******************************************************
		KieSession kieSession2 = kieSessionHandler.getRepositorySession(
				"spongecell", "testRelease", "0.0.1-SNAPSHOT", "");
		validateSession(kieSession2);
	}	

	@Test
	public void validateKieModuleMemoryFileSystem () {
		// This creates a module with the id: 
		// spongecell:core:0.0.1-SNAPSHOT to the repository
		//*************************************************
		KieSession kieSession1 = kieSessionHandler.buildKIESessionKModuleMemoryFileSystem(
			"spongecell", "core-alpha", "0.0.1-SNAPSHOT", 
			"heston-module-alpha", "heston-session-alpha");
		validateSession(kieSession1);
		
		// Now get the module from the repository and validate
		// that the rules fire correctly.
		//******************************************************
		KieSession kieSession2 = kieSessionHandler.getRepositorySession(
			"spongecell", "core-alpha", 
			"0.0.1-SNAPSHOT", "heston-session-alpha");
		validateSession(kieSession2);
		log.info("Test");
	}	
}	