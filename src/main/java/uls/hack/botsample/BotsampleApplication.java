package uls.hack.botsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"me.ramswaroop.jbot", "uls.hack.botsample"})
public class BotsampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotsampleApplication.class, args);
	}
}
