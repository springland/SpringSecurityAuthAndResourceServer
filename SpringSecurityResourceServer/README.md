###Spring Security Resource Server

This resource server runs on port 9090 , it talks to the authorization server at 8080

The project is using deprecaited spring security oauth2 project. 

To avoid that dependency the below is an example
@Configuration
public class ResourceServerConfig
extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
        .and()
        .oauth2ResourceServer(
            c -> c.opaqueToken(
                    o -> {
                        o.introspectionUri("…");
                        o.introspectionClientCredentials("client", "secret");
                    })
        );
    }
}

Three profiles
simple
jwt
jwtasym


 Get public key from authserver directly
   curl -u clientpassword:secret http://localhost:8080/oauth/token_key
