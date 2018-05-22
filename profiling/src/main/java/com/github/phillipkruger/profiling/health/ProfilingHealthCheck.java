package com.github.phillipkruger.profiling.health;

import com.github.phillipkruger.profiling.repository.ElasticsearchClient;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.transport.NoNodeAvailableException;

/**
 * Simple health check that test that the graphDB is available.
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Health
@ApplicationScoped
public class ProfilingHealthCheck implements HealthCheck {
    
    @Inject
    private ElasticsearchClient elasticsearchClient;
    
    @Override
    public HealthCheckResponse call() {
        
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("profiling");
        
        boolean up = elasticsearchClient.isHealthy();
        
        try{
            ClusterHealthResponse healthDetails = elasticsearchClient.getHealthDetails();
        
            responseBuilder = responseBuilder
                .withData("clusterName", healthDetails.getClusterName())
                .withData("numberOfDataNodes", healthDetails.getNumberOfDataNodes())
                .withData("numberOfNodes", healthDetails.getNumberOfNodes())
                .withData("status", healthDetails.getStatus().name())
                .withData("activeShardsPercent", String.valueOf(healthDetails.getActiveShardsPercent()) + "%");
        }catch(NoNodeAvailableException nnae){
            responseBuilder = responseBuilder
                .withData("exception", nnae.getMessage());
        }
        
        return responseBuilder.state(up).build();
        
    }
    
}