package com.github.phillipkruger.profiling;

import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

@Dependent
public class UserEventConverter {
    
    public JsonObject toJsonObject(UserEvent event){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        
        objectBuilder.add(USER_ID, event.getUserId());
        objectBuilder.add(EVENT_NAME, event.getEventName());
        objectBuilder.add(LOCATION, event.getLocation());
        objectBuilder.add(PARTNER_NAME, event.getPartnerName());
        objectBuilder.add(TIME_OCCURED, toString(event.getTimeOccured()));
        objectBuilder.add(TIME_RECEIVED, toString(event.getTimeReceived()));
        objectBuilder.add(DURATION_IN_MINUTES, event.getDurationInMinutes());
        
        Set<Map.Entry<String, String>> entrySet = event.getMetadata().entrySet();
        
        JsonObjectBuilder metadataBuilder = Json.createObjectBuilder();
        for(Map.Entry<String, String> entry:entrySet){
            metadataBuilder.add(entry.getKey(), entry.getValue());
        }
        
        objectBuilder.add(METADATA, metadataBuilder.build());
        
        return objectBuilder.build();
    }
    
    public String toJsonString(UserEvent event){
        JsonObject jsonObject = toJsonObject(event);
        return jsonObject.toString();
    }
    
    public UserEvent fromJsonObject(JsonObject jsonObject){
        UserEvent event = new UserEvent();
        event.setUserId(jsonObject.getJsonNumber(USER_ID).intValue());
        event.setEventName(jsonObject.getString(EVENT_NAME));
        event.setLocation(jsonObject.getString(LOCATION));
        event.setPartnerName(jsonObject.getString(PARTNER_NAME));
        event.setTimeOccured(toDate(jsonObject.getString(TIME_OCCURED)));
        event.setTimeReceived(toDate(jsonObject.getString(TIME_RECEIVED)));
        event.setDurationInMinutes(jsonObject.getJsonNumber(DURATION_IN_MINUTES).intValue());
        if(!jsonObject.isNull(METADATA)){
            JsonObject jsonMetadata = jsonObject.getJsonObject(METADATA);
            if(!jsonMetadata.isEmpty()){
                Set<String> keySet = jsonMetadata.keySet();
                for(String key:keySet){
                    event.addMetadata(key, jsonMetadata.getString(key));
                }
            }
        }
        
        return event;
    }
    
    public UserEvent fromJsonString(String json){
        try (JsonReader jsonReader = Json.createReader(new StringReader(json))) {
            JsonObject object = jsonReader.readObject();
            return fromJsonObject(object);
        }
    }
    
    public UserEvent fromJsonStream(InputStream is){
        try (JsonReader jsonReader = Json.createReader(is)) {
            JsonObject object = jsonReader.readObject();
            return fromJsonObject(object);
        }
    }
    
    public static final String USER_ID = "userId";
    public static final String EVENT_NAME = "eventName";
    public static final String LOCATION = "location";
    public static final String PARTNER_NAME = "partnerName";
    public static final String TIME_OCCURED = "timeOccured";
    public static final String TIME_RECEIVED = "timeReceived";
    public static final String DURATION_IN_MINUTES = "durationInMinutes";
    public static final String METADATA = "metaData";
    
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
    
    private Date toDate(String d){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(d);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private String toString(Date d){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(d);
    }
    
}