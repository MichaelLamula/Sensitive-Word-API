package Sensitive.Words.sensitve_words;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ComponentScan(basePackages = {"Sensitive.Words.sensitve_words", "security", "service", "controller", "config"})
@EntityScan(basePackages = {"entity"})
@EnableJpaRepositories(basePackages = {"repository"})
@ActiveProfiles("test")
class SensitveWordsApplicationTests {

	@Test
	void contextLoads() {
	}

}
