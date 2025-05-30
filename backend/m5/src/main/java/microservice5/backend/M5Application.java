package microservice5.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class M5Application {

	public static void main(String[] args) {
		SpringApplication.run(M5Application.class, args);
	}

}