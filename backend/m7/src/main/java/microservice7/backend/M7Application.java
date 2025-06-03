package microservice7.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class M7Application {

	public static void main(String[] args) {
		SpringApplication.run(M7Application.class, args);
	}

}