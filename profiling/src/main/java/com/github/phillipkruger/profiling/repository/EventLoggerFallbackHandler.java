package com.github.phillipkruger.profiling.repository;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.JsonObject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

@Log
@Dependent
public class EventLoggerFallbackHandler implements FallbackHandler<Future<Void>>{
    @Inject
    private Client client;
    
    @Inject
    private UserEventConverter converter;

    @Override
    public Future<Void> handle(ExecutionContext context) {
        UserEvent event = (UserEvent)context.getParameters()[1];
        
        // Maybe log a JIRA ? Notify someone ? 
        // In our case we save in another index...
        // We can later automatically save these events once the membership service is back up
        
        log.log(Level.SEVERE, ">>> Save error: log event [{0}] ...", event);
        
        JsonObject json = converter.toJsonObject(event);
        
        IndexResponse response = client.prepareIndex(IndexDetails.FAILURE_INDEX, IndexDetails.TYPE)
            .setSource(json.toString(), XContentType.JSON)
            .get();
        
        RestStatus status = response.status();
        
        log.log(Level.SEVERE, ">>> Status [{0}] ...", status.getStatus());
        
        return CompletableFuture.completedFuture(null);
    }
    
}
