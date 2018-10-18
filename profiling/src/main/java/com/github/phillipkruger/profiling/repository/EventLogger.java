package com.github.phillipkruger.profiling.repository;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import com.github.phillipkruger.profiling.eventstatus.Successful;
import com.github.phillipkruger.profiling.membership.Membership;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import lombok.extern.java.Log;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import com.github.phillipkruger.profiling.membership.MembershipProxy;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.elasticsearch.client.Client;

@RequestScoped
@Log
public class EventLogger {
    @Inject
    private Client client;
    
    @Inject
    private UserEventConverter converter;
    
    @Inject @Successful
    private Event<UserEvent> successfulBroadcaster;
    
    @Inject @RestClient
    private MembershipProxy membershipProxy;
    
    @Counted(name = "Events logged",absolute = true,monotonic = true)
    @Asynchronous
    @Retry(delay = 10, delayUnit = ChronoUnit.SECONDS, maxRetries = 5)
    @Fallback(EventLoggerFallbackHandler.class)
    public Future<Void> logEvent(String token, @NotNull UserEvent event){
        log.log(Level.SEVERE, ">>> Now (trying to )log event [{0}] ...", event);
        JsonObject json = converter.toJsonObject(event);
        int membershipId = json.getInt("userId");
        
        validateMembership(token,membershipId);
        
        IndexResponse response = client.prepareIndex(IndexDetails.PROFILING_INDEX, IndexDetails.TYPE)
            .setSource(json.toString(), XContentType.JSON)
            .get();

        RestStatus status = response.status();

        if(status.getStatus()==201){
            successfulBroadcaster.fire(event);
        }

        return CompletableFuture.completedFuture(null);
    }
    
    public void handleSuccessfulEvents(@Observes @Successful UserEvent userEvent){
        log.log(Level.INFO, ">>>>>>>>> Received event [{0}]", userEvent);
    }
    
    
    private void validateMembership(String token,int membershipId) {
        Membership membership = membershipProxy.getMembership("Bearer " + token, membershipId);
        log.log(Level.FINEST, "Validate membership = [{0}]", membership);
    }
}
