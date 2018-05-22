package com.github.phillipkruger.profiling.repository;

import com.github.phillipkruger.profiling.UserEvent;
import com.github.phillipkruger.profiling.UserEventConverter;
import com.github.phillipkruger.profiling.eventstatus.Successful;
import com.github.phillipkruger.profiling.eventstatus.Failed;
import com.github.phillipkruger.profiling.membership.Membership;
import com.github.phillipkruger.profiling.membership.MembershipProxy;
import com.github.phillipkruger.profiling.membership.MembershipStub;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import lombok.extern.java.Log;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;

@RequestScoped
@Log
public class EventLogger {
    @Inject
    private ElasticsearchClient client;
    
    @Inject
    private UserEventConverter converter;
    
    @Inject @Successful
    private Event<UserEvent> successfulBroadcaster;
    
    @Inject @Failed
    private Event<UserEvent> failedBroadcaster;
    
    @Inject
    private MembershipStub membershipStub;
    private MembershipProxy membershipProxy;
    
    
    public void init(){
        membershipProxy = membershipStub.getMembershipProxy();
    }
    
    @Counted(name = "Events logged",absolute = true,monotonic = true)
    @Asynchronous
    @Retry(abortOn = RuntimeException.class,delay = 30, delayUnit = ChronoUnit.SECONDS, maxRetries = 5)
    //@Fallback(StringFallbackHandler.class)
    public Future<Void> logEvent(@NotNull UserEvent event){
        
        if(client.isHealthy()){
            
            String json = converter.toJsonString(event);
            
            // TODO: validateMember(... get the id)
            
            try{
                IndexResponse response = client.getClient().prepareIndex(ElasticsearchClient.INDEX, ElasticsearchClient.TYPE)
                    .setSource(json, XContentType.JSON)
                    .get();

                RestStatus status = response.status();
                
                if(status.getStatus()==201){
                    successfulBroadcaster.fire(event);
                }else{
                    log.severe(">>>>>>>>> 1");
                    failedBroadcaster.fire(event);
                }
            }catch(NoNodeAvailableException nnae){
                log.severe(">>>>>>>>> 2");
                failedBroadcaster.fire(event);
            }
        }else{
            log.severe(">>>>>>>>> 3");
            failedBroadcaster.fire(event);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public void handleSuccessfulEvents(@Observes @Successful UserEvent userEvent){
        log.log(Level.INFO, ">>>>>>>>> Received event [{0}]", userEvent);
    }
    
    private boolean validateMember(int memberNumber){
        Membership membership = membershipProxy.getMembership(memberNumber);
        return membership!=null;
    }
    
}
