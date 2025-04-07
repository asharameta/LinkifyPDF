package supervisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import supervisor.model.PDFDataDAO;
import supervisor.service.LinkifyPDFService;

@Configuration
@ComponentScan("supervisor")
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("http://localhost:5500");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        // Handle async requests if needed
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public PDFDataDAO pdfDataDAO(){
        return new PDFDataDAO();
    }

    @Bean
    public LinkifyPDFService linkifyPDFService(PDFDataDAO data){
        return new LinkifyPDFService(data);
    }
}