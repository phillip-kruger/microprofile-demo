package com.github.phillipkruger.profiling.startup;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import com.github.phillipkruger.profiling.repository.IndexDetails;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Just load some test events so that we data to play with
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@ApplicationScoped
public class BootstrapData {
    
    @Inject
    private UserEventConverter userEventConverter;
    
    @Inject
    private TransportClient client;
    
    public void init(@Observes @Initialized(ApplicationScoped.class) Object init) {
        
        logEvent(createGymEvent(1));
        logEvent(createDeviceEvent(2));
        logEvent(createGymEvent(3));
        logEvent(createDeviceEvent(4));
        
    }
    
    private void logEvent(UserEvent event){
        try {
            client.prepareIndex(IndexDetails.INDEX, IndexDetails.TYPE)
                .setSource(userEventConverter.toJsonString(event), XContentType.JSON)
                .get();
        }catch(NoNodeAvailableException nnae){
            log.warning(nnae.getMessage());
        }
    }
    
    private UserEvent createGymEvent(int userId){
        
        UserEvent e = new UserEvent();
        e.setUserId(userId); // who did it ?
        e.setTimeOccured(new Date());
        e.setEventName("Gym"); // what happened ?
        e.setDurationInMinutes((int)Duration.ofMinutes(getRandomDuration()).toMinutes()); // for how long (optional)?
        e.setLocation(getRandomElement(townOptions)); // where did this happen (optional)?
        e.setPartnerName(getRandomElement(gymPartners)); // at what partner did this happen (optional)?
    
        Map<String,String> metadata = new HashMap<>(); // anyting else we need to know (optional) ?
        metadata.put("className", getRandomElement(gymClassOptions));
        
        e.setMetadata(metadata);
        
        return e;
    }
    
    private UserEvent createDeviceEvent(int userId){
        
        UserEvent e = new UserEvent();
        e.setUserId(userId); // who did it ?
        e.setTimeOccured(new Date());
        e.setEventName(getRandomElement(sportType)); // what happened ?
        e.setDurationInMinutes((int)Duration.ofMinutes(getRandomDuration()).toMinutes()); // for how long (optional)?
        e.setLocation(getRandomElement(townOptions)); // where did this happen (optional)?
        e.setPartnerName(getRandomElement(deviceOptions)); // at what partner did this happen (optional)?
    
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
    
    private static final String deviceOptions[] = new String[]{
        "Fitbit",
        "Garmin",
        "Jawbone",
        "Polar"
    };
    
    private static final String gymClassOptions[] = new String[]{
        "Spinning",
        "Aerobics",
        "Crossfit",
        "Swimming"
    };
    
    private static final String sportType[] = new String[]{
        "Walk",
        "Run",
        "Tennis",
        "Squash",
        "Badminton",
        "Volleyball",
        "Basketball",
        "Netball",
        "Baseball",
        "Cricket",
        "Gymnastics",
        "Surfing",
        "Dodgeball",
        "Hiking",
        "Cycling",
        "Karate",
        "Rugby",
        "Golf",
        "Triathlon",
        "Table tennis",
        "Hockey",
        "Hiking",
        "Canoeing",
        "Swimming",
        "CrossFit"
        
    };
    
    private static final String gymPartners[] = new String[]{
        "Virgin Active",
        "Planet Fitness"
    };
    
    private static final String townOptions[] = new String[]{
        "Johannesburg",
        "Pretoria",
        "Centurion",
        "Durban",
        "Cape Town",
        "Bloemfontein",
        "Klerksdorp",
        "Ballito",
        "Pofadder",
        "Port Elizabeth",
        "Pofadder",
        "Alberton",
        "Barberton",
        "Beaufort West",
        "Bela-Bela",
        "Benoni",
        "Bethlehem",
        "Calvinia",
        "Camps Bay",
        "Cato Ridge",
        "Ceres",
        "Dundee",
        "East London",
        "Empangeni",
        "Eshowe",
        "Franschhoek",
        "Ga-Rankuwa",
        "George",
        "Graaff-Reinet",
        "Harrismith",
        "Hluhluwe",
        "Jeffreys Bay",
        "Kempton Park",
        "Khayelitsha",
        "Kimberley",
        "Knysna",
        "Kroonstad",
        "KwaDukuza",
        "KwaMashu",
        "Ladysmith",
        "Lephalale",
        "Lydenburg",
        "Mamelodi",
        "Margate",
        "Midrand",
        "Modimolle",
        "Mokopane",
        "Mossel Bay",
        "Mtubatuba",
        "Newcastle",
        "Oudtshoorn",
        "Paarl",
        "Parys",
        "Pietermaritzburg",
        "Plettenberg Bay",
        "Polokwane",
        "Potchefstroom",
        "Randburg",
        "Richards Bay",
        "Rustenburg",
        "Sandton",
        "Sasolburg",
        "Somerset West",
        "Soweto",
        "Swellendam",
        "Tembisa",
        "Umhlanga Rocks",
        "Upington",
        "Umlazi",
        "Vanderbijlpark",
        "Verulam",
        "Vryburg",
        "Vryheid",
        "Welkom",
        "Witbank",
        "Worcester"};
    
}
