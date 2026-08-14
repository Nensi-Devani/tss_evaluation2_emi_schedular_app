package emi_schedular.example.tss_evaluation2_emi_schedular_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TssEvaluation2EmiSchedularAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TssEvaluation2EmiSchedularAppApplication.class, args);
	}

}
