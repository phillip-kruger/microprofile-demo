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
import org.elasticsearch.client.transport.TransportClient;
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
    
    @Getter
    private TransportClient client;
            
    @PostConstruct
    public void init(){
        try {
            
            Settings settings = Settings.builder()
                .put("cluster.name", clusterName).build();
            
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(hostName), hostPort));
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, null, ex);
        }   
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
