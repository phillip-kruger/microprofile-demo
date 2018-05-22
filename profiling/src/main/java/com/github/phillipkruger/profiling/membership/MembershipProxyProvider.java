package com.github.phillipkruger.profiling.membership;

import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Dependent
public class MembershipProxyProvider {
    
    @Produces
    public MembershipProxy getMembershipProxy(){
        try {
            URL apiUrl = new URL(membershipEndpoint);
                return RestClientBuilder.newBuilder()
                    .baseUrl(apiUrl)
                    .build(MembershipProxy.class);
        } catch (MalformedURLException ex) {
            throw new RuntimeException();
        }
    }
    
    @Inject @ConfigProperty(name = "membership.endpoint", defaultValue = "http://localhost:8080/membership/api")
    private String membershipEndpoint;
}