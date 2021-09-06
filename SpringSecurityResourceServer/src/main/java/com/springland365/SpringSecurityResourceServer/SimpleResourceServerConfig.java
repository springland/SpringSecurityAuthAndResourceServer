package com.springland365.SpringSecurityResourceServer;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
@Profile("simple")
public class SimpleResourceServerConfig {
}
