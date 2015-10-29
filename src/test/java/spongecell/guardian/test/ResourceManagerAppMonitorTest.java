package spongecell.guardian.test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import spongecell.guaradian.agent.yarn.ResourceManagerAppMonitor;
import spongecell.guaradian.agent.yarn.ResourceManagerAppMonitorConfiguration;

import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@ContextConfiguration(classes = { 
	ResourceManagerAppMonitorTest.class,
	ResourceManagerAppMonitor.class 
})
@EnableConfigurationProperties({})
public class ResourceManagerAppMonitorTest extends
		AbstractTestNGSpringContextTests {
	private @Autowired ResourceManagerAppMonitor resourceManagerAppMonitor;
	private static final String WORDCOUNT = "word count";

	@Test
	public void validateAppMonitorConfiguration() {
		final String CLUSTER = "ws/v1/cluster";
		final String ENDPOINT = "apps";
		final String SCHEME = "http";
		final String HOST = "dockerhadoop";
		final int PORT = 8088;

		String cluster = resourceManagerAppMonitor.getConfig().getCluster();
		Assert.assertNotNull(cluster);
		Assert.assertEquals(cluster, CLUSTER);

		String endPoint = resourceManagerAppMonitor.getConfig().getEndpoint();
		Assert.assertNotNull(endPoint);
		Assert.assertEquals(endPoint, ENDPOINT);

		String scheme = resourceManagerAppMonitor.getConfig().getScheme();
		Assert.assertNotNull(scheme);
		Assert.assertEquals(scheme, SCHEME);

		String host = resourceManagerAppMonitor.getConfig().getHost();
		Assert.assertNotNull(host);
		Assert.assertEquals(host, HOST);

		int port = resourceManagerAppMonitor.getConfig().getPort();
		Assert.assertNotNull(port);
		Assert.assertEquals(port, PORT);
	}

	@Test
	public void validateResourceManagerApps() throws IllegalStateException,
			IOException, InterruptedException {
		String state = ResourceManagerAppMonitorConfiguration.STATE;
		String app = ResourceManagerAppMonitorConfiguration.APP;
		String finalStatus = ResourceManagerAppMonitorConfiguration.FINAL_STATUS;
		String running = ResourceManagerAppMonitorConfiguration.RunStates.RUNNING.name();
		String unknown = ResourceManagerAppMonitorConfiguration.RunStates.UNKNOWN.name();
		String undefined = ResourceManagerAppMonitorConfiguration.RunStates.UNDEFINED.name();
		String finished = ResourceManagerAppMonitorConfiguration.RunStates.FINISHED.name();
		String succeeded = ResourceManagerAppMonitorConfiguration.RunStates.SUCCEEDED.name();
		JsonNode appStatus = null; 
		
		do { 
			log.info("*************** Getting the applications' status.***************");
			appStatus = resourceManagerAppMonitor.getResourceManagerAppStatus(WORDCOUNT);
			Assert.assertNotNull(appStatus);
		} while ((appStatus.get(app).get(state).asText().equals(unknown) && 
			   appStatus.get(app).get(finalStatus).asText().equals(unknown)) ||
			   ((appStatus.get(app).get(state).asText().equals(running) && 
			   appStatus.get(app).get(finalStatus).asText().equals(undefined))));
		
		Assert.assertEquals(appStatus.get(app).get(state).asText(),  finished);
		Assert.assertEquals(appStatus.get(app).get(finalStatus).asText(),  succeeded);
	}
}