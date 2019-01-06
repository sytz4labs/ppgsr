package us.ppgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="us.ppgs")
public class PSSApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PSSApplication.class, args);
	}
}
