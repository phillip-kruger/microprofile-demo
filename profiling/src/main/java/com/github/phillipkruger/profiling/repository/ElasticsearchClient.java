package com.github.phillipkruger.profiling.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Client for ES
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class ElasticsearchClient {
    
    public static final String INDEX = "profiling";
    public static final String TYPE = "event";
    
    private TransportClient client;
    
    @PostConstruct
    public void init(){
        this.client = getClient();
    }
    
    public TransportClient getClient() throws ClientNotAvailableException{
        if(this.client==null){
            try {
            
                Settings settings = Settings.builder()
                    .put("cluster.name", clusterName).build();

                this.client = new PreBuiltTransportClient(settings)
                        .addTransportAddress(new TransportAddress(InetAddress.getByName(hostName), hostPort));
            } catch (UnknownHostException ex) {
                log.log(Level.SEVERE, null, ex);
                throw new ClientNotAvailableException(ex);
            } 
        }
        if(this.client==null)throw new ClientNotAvailableException(new NullPointerException("Client is null"));
        return this.client;
    } 
    
    public boolean isHealthy(){
        try {
            ClusterHealthStatus status = getHealthDetails().getStatus();
            return status.equals(ClusterHealthStatus.GREEN) || status.equals(ClusterHealthStatus.YELLOW);
        }catch(NoNodeAvailableException nnae){
            log.severe(nnae.getMessage());
            return false;
        }
    }
    
    public ClusterHealthResponse getHealthDetails(){
        ClusterAdminClient clusterAdminClient = client.admin().cluster();
        return clusterAdminClient.prepareHealth().get();
    }
    
    @PreDestroy
    public void destroy(){
        if(client!=null)client.close();
    }
    
    @Inject @ConfigProperty(name = "elasticsearch.cluster.name", defaultValue = "the-red-cluster")
    private String clusterName;
    
    @Inject @ConfigProperty(name = "elasticsearch.host.name", defaultValue = "localhost")
    private String hostName;
    
    @Inject @ConfigProperty(name = "elasticsearch.host.port", defaultValue = "9300")
    private int hostPort;
    
}
