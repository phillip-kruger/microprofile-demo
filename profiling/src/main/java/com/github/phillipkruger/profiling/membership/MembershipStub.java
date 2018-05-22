package com.github.phillipkruger.profiling.membership;

import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@ApplicationScoped
public class MembershipStub {
    
    @Getter
    private MembershipProxy membershipProxy;
    
    @PostConstruct
    public void init(){
        try {
            URL apiUrl = new URL(membershipEndpoint);
                this.membershipProxy = RestClientBuilder.newBuilder()
                    .baseUrl(apiUrl)
                    .build(MembershipProxy.class);
        } catch (MalformedURLException ex) {
            throw new RuntimeException();
        }
    }
    
    @Inject @ConfigProperty(name = "membership.endpoint", defaultValue = "http://localhost:8080/membership/api")
    private String membershipEndpoint;
}
