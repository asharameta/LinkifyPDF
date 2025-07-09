package supervisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import supervisor.model.PdfEntityDAO;
import supervisor.model.SelectionEntityDAO;
import supervisor.service.LinkifyPDFService;
import org.springframework.jdbc.core.JdbcTemplate;

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
    public LinkifyPDFService linkifyPDFService(PdfEntityDAO pdfEntityDAO, SelectionEntityDAO selectionEntityDAO) {
        return new LinkifyPDFService(pdfEntityDAO, selectionEntityDAO);
    }
}