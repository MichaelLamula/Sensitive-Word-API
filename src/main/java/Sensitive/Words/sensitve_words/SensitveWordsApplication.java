package Sensitive.Words.sensitve_words;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"Sensitive.Words.sensitve_words", "security", "service", "controller", "config", "filter"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"repository"})
public class SensitveWordsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SensitveWordsApplication.class, args);
	}
}
