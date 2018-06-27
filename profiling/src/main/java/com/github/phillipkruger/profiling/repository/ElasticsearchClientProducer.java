package com.github.phillipkruger.profiling.repository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Produce a client for Elastic Search
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Dependent
public class ElasticsearchClientProducer {
    
    @Produces
    public TransportClient getClient() throws ClientNotAvailableException{
        try {
            Settings settings = Settings.builder()
                .put("cluster.name", clusterName).build();
            
            InetAddress inetAddress = InetAddress.getByName(hostName);
            
            TransportAddress transportAddress = new TransportAddress(inetAddress, hostPort);
            
            return new PreBuiltTransportClient(settings)
                    .addTransportAddress(transportAddress);
        } catch (UnknownHostException ex) {
            log.log(Level.SEVERE, null, ex);
            throw new ClientNotAvailableException(ex);
        } 
    } 
    
    @Inject @ConfigProperty(name = "elasticsearch.cluster.name", defaultValue = "the-red-cluster")
    private String clusterName;
    
    @Inject @ConfigProperty(name = "elasticsearch.host.name", defaultValue = "localhost")
    private String hostName;
    
    @Inject @ConfigProperty(name = "elasticsearch.host.port", defaultValue = "9300")
    private int hostPort;
}
