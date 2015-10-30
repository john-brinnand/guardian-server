package spongecell.guardian.agent.yarn;

import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter @Setter
@ConfigurationProperties(prefix ="eventhandler.scheduler")
public class ResourceManagerAppMonitorSchedulerConfiguration {
	public Integer initialDelay = 1;
	public Integer period = 3000;
	public TimeUnit timeUnit = TimeUnit.MILLISECONDS;
	
}
