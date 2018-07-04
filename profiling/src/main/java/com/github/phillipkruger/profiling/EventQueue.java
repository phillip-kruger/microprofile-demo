package com.github.phillipkruger.profiling;

import com.github.phillipkruger.profiling.repository.EventLogger;
import com.github.phillipkruger.profiling.eventstatus.Failed;
import com.github.phillipkruger.profiling.eventstatus.Successful;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.java.Log;

@ApplicationScoped
@Log
public class EventQueue {
    
    @Inject
    private EventLogger eventLogger;
    
    private final Queue<UserEvent> queue = new ConcurrentLinkedQueue<>();
    
    public void handleFailedEvents(@Observes @Failed UserEvent userEvent){
        log.log(Level.SEVERE, "Queueing event [{0}]", userEvent);
        queue.add(userEvent);
    }
    
    public void handleSuccessfulEvents(@Observes @Successful UserEvent userEvent){
        // Seems like we are back up and running, let's empty the queue
        if(!queue.isEmpty()){
            log.log(Level.SEVERE, "De-Queueing all events");
            List<UserEvent> all = new ArrayList<>(queue);
            queue.clear();

            for(UserEvent event:all){
                //eventLogger.logEvent(event);
            }
        }
        
    }
}
