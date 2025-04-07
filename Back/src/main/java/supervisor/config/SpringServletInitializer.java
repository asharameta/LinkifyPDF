package supervisor.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpringServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{AppConfig.class}; // Root context config classes (if needed)
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{AppConfig.class}; // Your Spring configuration class
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"}; // URL pattern for dispatcher servlet
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        Path projectRootPath = Paths.get(System.getProperty("user.dir"));
        Path pdfDirectoryPath = projectRootPath.resolve("storedPDFFiles");

        if (Files.notExists(pdfDirectoryPath)) {
            try {
                Files.createDirectories(pdfDirectoryPath); // Create the directory if it doesn't exist
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                pdfDirectoryPath.toString(), // temp location, or provide "/tmp"
                10 * 1024 * 1024, // maxFileSize
                20 * 1024 * 1024, // maxRequestSize
                0 // fileSizeThreshold
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}