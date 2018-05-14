package com.github.phillipkruger.profiling;

import com.github.phillipkruger.profiling.repository.ElasticsearchClient;
import java.io.StringReader;
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
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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
@Tag(name = "Profile service",description = "Build up a profile of the user")
public class ProfileService {
    
    @Inject
    private Counter aCounter;
    
    @Inject
    private ElasticsearchClient client;
    
    @Counted(name = "Events logged",absolute = true,monotonic = true)
    @POST
    public void logEvent(@NotNull @RequestBody(description = "Log a new event.",content = @Content(mediaType = MediaType.APPLICATION_JSON,schema = @Schema(implementation = Event.class))) 
                        Event event){
        
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
    
    @GET @Path("user/{userId}")
    @Timed(name = "Getting event requests time",absolute = true,unit = MetricUnits.MICROSECONDS)
    @Operation(description = "Getting all the events for a certain user")
    public Response getUserEvents(@PathParam("userId") int userId, @DefaultValue("-1") @QueryParam("size") int size){
        return search(USER_ID,userId,size);
    }
    // TODO: Add user per day
    
    @GET @Path("event/{eventName}")
    public Response searchEvents(@PathParam("eventName") String eventName, @DefaultValue("-1") @QueryParam("size") int size){
        return search(EVENT_NAME,eventName,size);
    }
    
    @GET @Path("location/{location}")
    public Response searchLocations(@PathParam("location") String location, @DefaultValue("-1") @QueryParam("size") int size){
        return search(LOCATION,location,size);
    }
    
    @GET @Path("partner/{partner}")
    public Response searchPartners(@PathParam("partner") String partner, @DefaultValue("-1") @QueryParam("size") int size){
        return search(PARTNER_NAME,partner,size);
    }
    
    private Response search(String key,Object value,int size){
        if(size<0)size=defaultResponseSize;
        try{
            SearchResponse response = getSearchRequestBuilder(key,value,size).get();
        
            return handleSearchResponse(response);
        }catch(NoNodeAvailableException nnae){
            // TODO:...
            nnae.printStackTrace();
            return Response.serverError().build();    
        }
    }
    
    private Response handleSearchResponse(SearchResponse response){
        int status = response.status().getStatus();
        log.severe("status is " + status);
        if(status == 200){
            SearchHits hits = response.getHits();
            long numberOfHits = hits.totalHits;
            log.severe("numberOfHits is " + numberOfHits);
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
    }
    
    private SearchRequestBuilder getSearchRequestBuilder(String key,Object value,int size){
        SearchRequestBuilder srb = client.getClient().prepareSearch(INDEX).setTypes(TYPE);
        srb = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb = srb.setQuery(QueryBuilders.termQuery(key, value));
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
