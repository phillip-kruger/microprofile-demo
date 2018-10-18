package com.github.phillipkruger.profiling.repository;

import java.io.StringReader;
import java.time.temporal.ChronoUnit;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

@RequestScoped
@Log
public class EventSearcher {
    
    @Inject
    private Client client;
    
    @Timeout(value = 5 , unit = ChronoUnit.SECONDS)
    @CircuitBreaker(failOn = NoNodeAvailableException.class,requestVolumeThreshold = 4, failureRatio=0.75, delay = 5, delayUnit = ChronoUnit.SECONDS )
    public Response search(String key,Object value,int size){
        if(size<0)size=defaultResponseSize;
        SearchResponse response = getSearchRequestBuilder(key,value,size).get();
        return handleSearchResponse(response);
    }
    
    private Response handleSearchResponse(SearchResponse response){
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
    }
    
    private SearchRequestBuilder getSearchRequestBuilder(String key,Object value,int size){
        SearchRequestBuilder srb = client.prepareSearch(IndexDetails.PROFILING_INDEX).setTypes(IndexDetails.TYPE);
        srb = srb.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        srb = srb.setQuery(QueryBuilders.termQuery(key, value));
        srb = srb.addSort(SortBuilders.fieldSort("timeOccured").order(SortOrder.DESC));
        srb = srb.setFrom(0).setSize(size);
        return srb;
    }
    
    @Inject @ConfigProperty(name = "default.response.size", defaultValue = "100")
    private int defaultResponseSize;
}
