package microservice1.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class M1Application {

	public static void main(String[] args) {
		SpringApplication.run(M1Application.class, args);
	}

}