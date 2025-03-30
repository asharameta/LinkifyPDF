package supervisor.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return null; // Root context config classes (if needed)
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{AppConfig.class}; // Your Spring configuration class
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"}; // URL pattern for dispatcher servlet
    }
}