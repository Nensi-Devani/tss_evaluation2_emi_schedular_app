package emi_schedular.example.tss_evaluation2_emi_schedular_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TssEvaluation2EmiSchedularAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TssEvaluation2EmiSchedularAppApplication.class, args);
	}

}
