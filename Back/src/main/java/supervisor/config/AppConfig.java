package supervisor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import supervisor.model.PDFData;
import supervisor.model.PDFDataDAO;
import supervisor.service.LinkifyPDFService;

@Configuration
@ComponentScan("supervisor")
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

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