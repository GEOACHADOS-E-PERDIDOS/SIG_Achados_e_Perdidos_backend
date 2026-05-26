package ifb.edu.br.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class EnvConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer dotenvConfig() {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        Properties props = new Properties();
        dotenv.entries().forEach(e ->
                props.put(e.getKey(), e.getValue())
        );

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setProperties(props);

        return configurer;
    }
}