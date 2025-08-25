package supervisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import supervisor.model.DocumentEntityDAO;
import supervisor.model.SelectionEntityDAO;
import supervisor.service.LinkifyPDFService;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
    

    @Bean
    public LinkifyPDFService linkifyPDFService(DocumentEntityDAO documentEntityDAO, SelectionEntityDAO selectionEntityDAO) {
        return new LinkifyPDFService(documentEntityDAO, selectionEntityDAO);
    }
}