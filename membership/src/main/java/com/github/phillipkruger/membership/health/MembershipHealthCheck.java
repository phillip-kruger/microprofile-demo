package com.github.phillipkruger.membership.health;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped
public class MembershipHealthCheck implements HealthCheck {

    
    @Override
    public HealthCheckResponse call() {
        
        // Check if the DB is up
        
        return HealthCheckResponse.named("membership")
                //.withData("ejb",pingEJB.getPing())
                //.withData("cdi", pingCDI.getPing())
                //.withData("config", ping)
                .up()
                .build();

   }
}
