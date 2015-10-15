package spongecell.guardian.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import spongecell.guardian.notification.SlackGuardianWebHook;
import spongecell.guardian.notification.SlackGuardianWebHookConfiguration;

/**
 * @author jbrinnand
 */
@Slf4j
@ContextConfiguration(classes = { SlackGuardianWebHookTest.class})
@EnableConfigurationProperties ({ SlackGuardianWebHook.class })
public class SlackGuardianWebHookTest extends AbstractTestNGSpringContextTests{
	private StringEntity greetingEntity = null; 
	private @Autowired SlackGuardianWebHook slackGuardianWebHook;

	@PostConstruct
	public void postConstruct() throws URISyntaxException, UnsupportedEncodingException {
		greetingEntity = new StringEntity("payload={ " +
		  "'text': 'This is posted to #guardian and comes from a bot named guardian.'" + 
		  "'icon_emoji': ':ghost:' }", ContentType.create("text/plain", "UTF-8"));
	}

	@Test
	public void slackGuardianWebHookGreeting() throws ClientProtocolException, IOException {
		CloseableHttpResponse response = slackGuardianWebHook.create(greetingEntity);
		log.info("Response status code {} ", response.getStatusLine().getStatusCode());
		response.close();
	}
}	