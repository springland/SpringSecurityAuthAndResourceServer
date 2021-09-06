package com.springland365.SpringSecurityAuthServer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

@Configuration
@EnableAuthorizationServer
@Profile("simple")
public class SimpleAuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    protected AuthenticationManager authenticationManager;



    /*
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        var service = new InMemoryClientDetailsService();
        var clientDetail = new BaseClientDetails();
        clientDetail.setClientId("client");
        clientDetail.setClientSecret("secret");
        clientDetail.setScope(List.of("read"));
        clientDetail.setAuthorizedGrantTypes(List.of("password"));
        service.setClientDetailsStore(Map.of("client" , clientDetail));

        clients.withClientDetails(service);


    }

     */

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client")
                .secret("secret")
                .authorizedGrantTypes("authorization_code" , "refresh_token")
                .redirectUris("http://localhost:9090")
                .scopes("read")
                .and()
                .withClient("clientpassword")
                .secret("secret")
                .authorizedGrantTypes("password" , "refresh_token")
                .redirectUris("http://localhost:9090")
                .and()
                .withClient("client_credential")
                .secret("secret")
                .authorizedGrantTypes("client_credentials")
                .scopes("info");
        ;

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("isAuthenticated()");
    }

}
