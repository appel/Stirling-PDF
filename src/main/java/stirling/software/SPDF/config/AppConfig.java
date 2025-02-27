package stirling.software.SPDF.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	
	
	@Bean(name = "rateLimit")
    public boolean rateLimit() {
        String appName = System.getProperty("rateLimit");
        if (appName == null) 
            appName = System.getenv("rateLimit");
        System.out.println("rateLimit=" + appName);
        return (appName != null) ? Boolean.valueOf(appName) : false;
    }
	
	@Bean(name = "loginEnabled")
    public boolean loginEnabled() {
        String appName = System.getProperty("login.enabled");
        if (appName == null) 
            appName = System.getenv("login.enabled");
        System.out.println("loginEnabled=" + appName);
        return (appName != null) ? Boolean.valueOf(appName) : false;
    }
	
    @Bean(name = "appName")
    public String appName() {
        String appName = System.getProperty("APP_HOME_NAME");
        if (appName == null)
            appName = System.getenv("APP_HOME_NAME");
        return (appName != null) ? appName : "Stirling PDF";
    }

    @Bean(name = "appVersion")
    public String appVersion() {
        String version = getClass().getPackage().getImplementationVersion();
        return (version != null) ? version : "0.0.0";
    }

    @Bean(name = "homeText")
    public String homeText() {
        String homeText = System.getProperty("APP_HOME_DESCRIPTION");
        if (homeText == null)
            homeText = System.getenv("APP_HOME_DESCRIPTION");
        return (homeText != null) ? homeText : "null";
    }

    @Bean(name = "navBarText")
    public String navBarText() {
        String navBarText = System.getProperty("APP_NAVBAR_NAME");
        if (navBarText == null)
            navBarText = System.getenv("APP_NAVBAR_NAME");
        if (navBarText == null)
            navBarText = System.getProperty("APP_HOME_NAME");
        if (navBarText == null)
            navBarText = System.getenv("APP_HOME_NAME");

        return (navBarText != null) ? navBarText : "Stirling PDF";
    }
}