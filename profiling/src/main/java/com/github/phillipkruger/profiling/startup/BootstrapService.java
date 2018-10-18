package com.github.phillipkruger.profiling.startup;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import com.github.phillipkruger.profiling.repository.ClientNotAvailableException;
import com.github.phillipkruger.profiling.repository.IndexDetails;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.InternalSettingsPreparer;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.transport.Netty4Plugin;

/**
 * Start an internal elasticsearch service, and load some test events so that we data to play with
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class BootstrapService {
    private static boolean isRunning = false;
    
    @Inject
    private UserEventConverter userEventConverter;
    
    @Inject @ConfigProperty(name = "java.io.tmpdir", defaultValue = "/tmp")
    private String tempDir; 
    
    @Inject @ConfigProperty(name = "elasticsearch.cluster.name", defaultValue = IndexDetails.CLUSTER_NAME)
    private String clusterName;
    
    @Resource(lookup="java:app/AppName")
    private String appName;
    
    private Node node = null;
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        
        startElastic();
        
        logEvent(createGymEvent(1));
        logEvent(createDeviceEvent(1));
        
        logEvent(createGymEvent(2));
        logEvent(createDeviceEvent(2));
        
        logEvent(createGymEvent(3));
        logEvent(createDeviceEvent(3));
        
        logEvent(createGymEvent(4));
        logEvent(createDeviceEvent(4));
        
    }
    
    private void startElastic(){
        if(!isRunning){
            
            String homePath = tempDir + SLASH + appName + SLASH;
            
            log.log(Level.INFO, "====== Starting Elasticsearch server ====== ");
            log.log(Level.INFO, "====== path.home [{0}] ====== ", homePath);
            log.log(Level.INFO, "====== cluster.name [{0}] ====== ", clusterName);
            Settings.Builder settingsBuilder = Settings.builder()
                    .put("path.home", homePath)
                    .put("cluster.name", clusterName)
                    .put("node.name", "internal")
                    .put("client.transport.sniff", true)
                    .put("node.max_local_storage_nodes",3);
                    

            Settings settings = settingsBuilder.build();

            node = new PluginNode(settings);
            try {
                node = node.start();
            } catch (NodeValidationException ex) {
                throw new RuntimeException(ex);
            }
            log.log(Level.INFO, "====== Elasticsearch is running ====== ");
            
            isRunning = true;
            
        }
    }
    
    private void logEvent(UserEvent event){
        try {
            getClient().prepareIndex(IndexDetails.PROFILING_INDEX, IndexDetails.TYPE)
                    .setSource(userEventConverter.toJsonString(event), XContentType.JSON)
                    .get();
            
        }catch(NoNodeAvailableException nnae){
            log.warning(nnae.getMessage());
        }
    }
    
    @Produces
    public Client getClient() throws ClientNotAvailableException{
        return node.client();
    }
    
    private UserEvent createGymEvent(int userId){
        
        UserEvent e = new UserEvent();
        e.setUserId(userId); // who did it ?
        e.setTimeOccured(new Date());
        e.setEventName("Gym"); // what happened ?
        e.setDurationInMinutes((int)Duration.ofMinutes(getRandomDuration()).toMinutes()); // for how long (optional)?
        e.setLocation(getRandomElement(BootstrapData.townOptions)); // where did this happen (optional)?
        e.setPartnerName(getRandomElement(BootstrapData.gymPartners)); // at what partner did this happen (optional)?
    
        Map<String,String> metadata = new HashMap<>(); // anyting else we need to know (optional) ?
        metadata.put("className", getRandomElement(BootstrapData.gymClassOptions));
        
        e.setMetadata(metadata);
        
        return e;
    }
    
    private UserEvent createDeviceEvent(int userId){
        
        UserEvent e = new UserEvent();
        e.setUserId(userId); // who did it ?
        e.setTimeOccured(new Date());
        e.setEventName(getRandomElement(BootstrapData.sportType)); // what happened ?
        e.setDurationInMinutes((int)Duration.ofMinutes(getRandomDuration()).toMinutes()); // for how long (optional)?
        e.setLocation(getRandomElement(BootstrapData.townOptions)); // where did this happen (optional)?
        e.setPartnerName(getRandomElement(BootstrapData.deviceOptions)); // at what partner did this happen (optional)?
    
        Map<String,String> metadata = new HashMap<>(); // anyting else we need to know (optional) ?
        metadata.put("calories", String.valueOf(getRandomCalories()));
        
        e.setMetadata(metadata);
        
        return e;
    }
    
    private String getRandomElement(String[] options){
        int i = getRandomInt(0,options.length-1);
        return options[i];
    }
    
    private int getRandomDuration(){
        return getRandomInt(30, 91);
    }
    
    private int getRandomCalories(){
        return getRandomInt(295, 750);
    }
    
    private int getRandomInt(int min,int max){
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }
    
    static class PluginNode extends Node {
        public PluginNode(Settings settings) {
            super(InternalSettingsPreparer.prepareEnvironment(settings, null), Collections.singletonList(Netty4Plugin.class));
        }
    }
    
    private static final String SLASH = "/";
}
