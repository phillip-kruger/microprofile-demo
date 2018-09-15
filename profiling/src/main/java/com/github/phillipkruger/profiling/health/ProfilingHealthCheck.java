package com.github.phillipkruger.profiling.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

/**
 * Simple health check that test that the elasticsearch server is available.
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Health
@ApplicationScoped
public class ProfilingHealthCheck implements HealthCheck {
    
    @Inject
    private TransportClient client;
    
    @Override
    public HealthCheckResponse call() {
        
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("profiling");
        
        try{
            ClusterHealthResponse clusterHealth = getHealthDetails();
            
            boolean up = isHealthy(clusterHealth);
            responseBuilder = responseBuilder
                .withData("clusterName", clusterHealth.getClusterName())
                .withData("numberOfDataNodes", clusterHealth.getNumberOfDataNodes())
                .withData("numberOfNodes", clusterHealth.getNumberOfNodes())
                .withData("status", clusterHealth.getStatus().name())
                .withData("activeShardsPercent", String.valueOf(clusterHealth.getActiveShardsPercent()) + "%");
            
            return responseBuilder.state(up).build();
            
        }catch(NoNodeAvailableException nnae){
            responseBuilder = responseBuilder
                .withData("exception", nnae.getMessage());
            
            return responseBuilder.state(false).build();
        }
        
    }
    
    
    private ClusterHealthResponse getHealthDetails(){
        ClusterAdminClient clusterAdminClient = client.admin().cluster();
        return clusterAdminClient.prepareHealth().get();
    }
    
    private boolean isHealthy(ClusterHealthResponse clusterHealth){
        ClusterHealthStatus status = clusterHealth.getStatus();
        return status.equals(ClusterHealthStatus.GREEN) || status.equals(ClusterHealthStatus.YELLOW);
    }
    
}