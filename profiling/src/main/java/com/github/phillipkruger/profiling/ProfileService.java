package com.github.phillipkruger.profiling;

import com.github.phillipkruger.profiling.repository.ElasticsearchClient;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

/**
 * Profiling Service. JAX-RS
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
public class ProfileService {
    
    @Inject
    private ElasticsearchClient client;
    
    @POST
    public void logEvent(@NotNull Event event){
        JsonObject jo = toJsonObject(event);
        String json = jo.toString();
        
        log.log(Level.FINEST, ">>> Now indexing:\n{0}", json);
        
        try{
            IndexResponse response = client.getClient().prepareIndex(INDEX, TYPE)
                .setSource(json, XContentType.JSON)
                .get();
        
            RestStatus status = response.status();
            // TODO: Fire CDI event and create Metrics
            if(status.getStatus()==201){
            
            }else{
            
            }
        }catch(NoNodeAvailableException nnae){
            // TODO: Fire CDI event and create Metrics
        }
        
    }
    
    @GET @Path("{userId}")
    public Response getUserEvents(@PathParam("userId") int userId, @DefaultValue("-1") @QueryParam("size") int size){

        if(size<0)size=defaultResponseSize;
        try{
            SearchResponse response = getSearchRequestBuilder(userId,size).get();
        
            int status = response.status().getStatus();
            if(status == 200){
                SearchHits hits = response.getHits();
                long numberOfHits = hits.totalHits;
                if(numberOfHits>0){
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                    long took = response.getTook().getMillis();
                
                    SearchHit[] hitsArray = hits.getHits();

                    for(SearchHit hit:hitsArray){
                        String json = hit.getSourceAsString();
                        
                        JsonReader jsonReader = Json.createReader(new StringReader(json));
                        JsonObject eventJson = jsonReader.readObject();        

                        arrayBuilder.add(eventJson);
                        
                    }
                    return Response.ok(arrayBuilder.build())
                                .header("x-number-of-hits", numberOfHits)
                                .header("x-time-took-ms", took)
                                .build();    
                }else{
                    // TODO:...
                    return Response.serverError().build();    
                }
            }else{
                // TODO:...
                return Response.serverError().build();    
            }
        }catch(NoNodeAvailableException nnae){
            // TODO:...
            nnae.printStackTrace();
            return Response.serverError().build();    
        }
    }
    
    private SearchRequestBuilder getSearchRequestBuilder(int userId,int size){
        SearchRequestBuilder srb = client.getClient().prepareSearch(INDEX).setTypes(TYPE);
        srb = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb = srb.setQuery(QueryBuilders.termQuery(USER_ID, userId));
        //srb = srb.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
        srb = srb.setFrom(0).setSize(size);
        
        return srb;
    }
    
    private JsonObject toJsonObject(Event event){
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        
        objectBuilder.add(USER_ID, event.getUserId());
        objectBuilder.add(EVENT_NAME, event.getEventName());
        objectBuilder.add(LOCATION, event.getLocation());
        objectBuilder.add(PARTNER_NAME, event.getPartnerName());
        objectBuilder.add(TIME_OCCURED, event.getTimeOccured().format(dtf));
        objectBuilder.add(TIME_RECEIVED, event.getTimeReceived().format(dtf));
        objectBuilder.add(DURATION_IN_SECONDS, event.getDuration().getSeconds());
        
        Map<String, String> metadata = event.getMetadata();
        Set<Map.Entry<String, String>> entrySet = metadata.entrySet();
        
        JsonObjectBuilder metadataBuilder = Json.createObjectBuilder();
        for(Map.Entry<String, String> entry:entrySet){
            metadataBuilder.add(entry.getKey(), entry.getValue());
        }
        
        objectBuilder.add(METADATA, metadataBuilder.build());
        
        return objectBuilder.build();
    }
    
    private Event toEvent(String json){
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject jobj = jsonReader.readObject();  


        int userId = jobj.getInt(USER_ID);
        String eventName = jobj.getString(EVENT_NAME);
        String location = jobj.getString(LOCATION);
        String partnerName = jobj.getString(PARTNER_NAME);
        String timeOccured = jobj.getString(TIME_OCCURED);
        String timeReceived = jobj.getString(TIME_RECEIVED);
        int durationInSeconds = jobj.getInt(DURATION_IN_SECONDS);
        
        Event e = new Event();
        e.setUserId(userId);
        e.setEventName(eventName);
        e.setLocation(location);
        e.setPartnerName(partnerName);
        if(timeOccured!=null && !timeOccured.isEmpty())e.setTimeOccured(LocalDateTime.parse(timeOccured, dtf));
        if(timeReceived!=null && !timeReceived.isEmpty())e.setTimeReceived(LocalDateTime.parse(timeReceived, dtf));
        e.setDuration(Duration.ofSeconds(durationInSeconds));
        
        // TODO: Metadata
        return e;
    }
    
    @Inject @ConfigProperty(name = "default.response.size", defaultValue = "100")
    private int defaultResponseSize;
    
    private static final String INDEX = "profiling";
    private static final String TYPE = "event";
 
    private static final String USER_ID = "userId";
    private static final String EVENT_NAME = "eventName";
    private static final String LOCATION = "location";
    private static final String PARTNER_NAME = "partnerName";
    private static final String TIME_OCCURED = "timeOccured";
    private static final String TIME_RECEIVED = "timeReceived";
    private static final String DURATION_IN_SECONDS = "durationInSeconds";
    private static final String METADATA = "metaData";
    
    private static final DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
}
