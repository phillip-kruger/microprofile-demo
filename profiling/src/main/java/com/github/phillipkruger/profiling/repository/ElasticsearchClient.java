package com.github.phillipkruger.profiling.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.extern.java.Log;
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
                .put("cluster.name", "the-red-cluster").build();
            
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, null, ex);
        }   
    }
    
    @PreDestroy
    public void destroy(){
        if(client!=null)client.close();
    }
}
