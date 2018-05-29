package com.github.phillipkruger.user.health;

import javax.enterprise.context.ApplicationScoped;
import lombok.extern.java.Log;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

/**
 * Simple health check that test  ???????????????
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Health
@ApplicationScoped
public class UserHealthCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("user");
        return responseBuilder.up().build();   
    }
}