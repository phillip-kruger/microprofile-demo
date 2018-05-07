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
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.cluster.health.ClusterHealthStatus;

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
        
        boolean up =false;
        ClusterAdminClient clusterAdminClient = elasticsearchClient.getClient().admin().cluster();
        try{
            ClusterHealthResponse healths = clusterAdminClient.prepareHealth().get();
        
            ClusterHealthStatus status = healths.getStatus();
            if(status.equals(ClusterHealthStatus.GREEN) || status.equals(ClusterHealthStatus.YELLOW))up = true;
            
            responseBuilder = responseBuilder
                .withData("clusterName", healths.getClusterName())
                .withData("numberOfDataNodes", healths.getNumberOfDataNodes())
                .withData("numberOfNodes", healths.getNumberOfNodes())
                .withData("status", status.name())
                .withData("activeShardsPercent", String.valueOf(healths.getActiveShardsPercent()) + "%");
        }catch(NoNodeAvailableException nnae){
            responseBuilder = responseBuilder
                .withData("exception", nnae.getMessage());
        }
        
        return responseBuilder.state(up).build();
        
    }
    
    
    
}
