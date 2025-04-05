package supervisor.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import jakarta.servlet.ServletRegistration.Dynamic;

import java.io.File;

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
        // Get the root directory of the project
        String projectRootPath = System.getProperty("user.dir");

        String pdfDirectoryPath = projectRootPath +
                                  File.separator +
                                  "storedPDFFiles";

        // Make sure the directory exists
        File tempDir = new File(pdfDirectoryPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs(); // Create the directory if it doesn't exist
        }

        System.out.println(pdfDirectoryPath);

        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                pdfDirectoryPath, // temp location, or provide "/tmp"
                10 * 1024 * 1024, // maxFileSize
                20 * 1024 * 1024, // maxRequestSize
                0 // fileSizeThreshold
        );
        registration.setMultipartConfig(multipartConfigElement);
    }
}