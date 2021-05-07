package us.ppgs.mirror.client;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MirrorClient {

	@Autowired
	private MirrorThread thread;
	private Future<String> status = null;
	
	public boolean isRunning() {
		return !status.isDone();
	}
	
	@Scheduled(cron="1 0 12 * * *")
	public boolean schedule() {
		
		if (status == null || status.isDone()) {
			status  = thread.run();
			return true;
		}

		return false;
	}
}
