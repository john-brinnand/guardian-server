package spongecell.guardian.test;

import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import spongecell.guardian.model.Person;

@Slf4j
@ContextConfiguration(classes = { KIESpringBusinessRulesTest.class, KieSessionHandler.class })
@EnableConfigurationProperties ({ })
public class KIESpringBusinessRulesTest extends AbstractTestNGSpringContextTests {
	private @Autowired KieSessionHandler kieSessionHandler;
	private static final String PATH = "testPath";
	
	@Test
	public void validateKieSessionHandler() {
		String[] rules = kieSessionHandler.getRules();
		Assert.assertNotNull(rules);
		
		String path = kieSessionHandler.getPath();
		Assert.assertEquals(path, PATH);
		
		log.info("KIE Session id is : {} ", 
			kieSessionHandler.getKieSession().getId());
		KieRepository kieRepository = kieSessionHandler.getKieRepository();
		log.info("Module is: {} ", kieRepository.getKieModule(kieRepository.getDefaultReleaseId()));
	}
	
	@Test
	public void validateConfigurableKieFileSystem () {
		Person person = new Person("Joe Test", 22);
		
		KieSession kieSession = kieSessionHandler.buildKIEFromKModuleFile();
		Assert.assertNotNull(kieSession);
		kieSession.insert(person);
		kieSession.fireAllRules();
		
		KieSession kieSession1 = kieSessionHandler.buildKIESessionInternal();
		Assert.assertNotNull(kieSession1);
		kieSession1.insert(person);
		kieSession1.fireAllRules();
	}
}	