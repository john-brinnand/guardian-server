package spongecell.guardian.agent.yarn;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;

@Slf4j
@Getter
@EnableConfigurationProperties({ ResourceManagerAppMonitorSchedulerConfiguration.class })
public class ResourceManagerAppMonitorScheduler {
	@Autowired 
	ResourceManagerAppMonitorSchedulerConfiguration config;
	@Autowired 
	private final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

	/**
	 * Start the data load.
	 * 
	 * @return
	 * @throws TimeoutException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public ScheduledFuture<?> loadData() throws TimeoutException, 
		InterruptedException, ExecutionException {
		
		// Note: the topic and groupId must be set in 
		// the environment. Otherwise default properties
		// will be in effect and data may not be received
		// from Kafka.
		//************************************************
		Integer initialDelay = config.getInitialDelay();
		Integer period = config.getPeriod();
		TimeUnit timeUnit = config.getTimeUnit();

		ScheduledFuture<?> future = pool.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				final long endTime;
				final long startTime = System.currentTimeMillis();
				endTime = System.currentTimeMillis();
				log.info("------------------  Monitor Completed  in {} {} ", 
					endTime - startTime, TimeUnit.MILLISECONDS.toString().toLowerCase());
			}
		}, initialDelay, period, timeUnit);
		
		return future;
	}

	/**
	 * Shutdown the executor.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		pool.shutdown();
		pool.awaitTermination(5000, TimeUnit.MILLISECONDS);
		if (!pool.isShutdown()) {
			log.info("Pool is not shutdown.");
			pool.shutdownNow();
		}
		Assert.isTrue(pool.isShutdown());
		log.info("Pool shutdown status : {}", pool.isShutdown());
	}
}
