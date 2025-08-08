package hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = {"hub", "com.example.common"}
)
public class HubApplication {
    public static void main(String[] args) {
        SpringApplication.run(HubApplication.class, args);
    }
}
