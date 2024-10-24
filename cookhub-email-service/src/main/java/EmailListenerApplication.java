import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ru.kovalenkojuls.emailservice")
public class EmailListenerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmailListenerApplication.class, args);
    }
}
