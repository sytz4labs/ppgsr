package us.ppgs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ExternalStaticResources {

    @Bean
    public WebMvcConfigurer webMvcConfigurerAdapter() {
    	
    	boolean isWin = System.getProperty("os.name").toLowerCase().indexOf("win") >= 0;
        String filePath = isWin ? "/my/" : "~/www/";
        String pathPatterns = "/pub/**";

        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                if (!registry.hasMappingForPattern(pathPatterns)) {
                    registry.addResourceHandler(pathPatterns)
                            .addResourceLocations("file:" + filePath);
                }
            }
        };
    }
}
