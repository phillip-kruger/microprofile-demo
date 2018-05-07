package com.github.phillipkruger.profiling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class Event  {
    
    @NotNull
    private int userId; // who did it ?
            
    @NotNull
    private LocalDateTime timeOccured; // when did this happen ?
    private LocalDateTime timeReceived = LocalDateTime.now(); // when did we hear about it ?
    
    @NotNull
    private String eventName; // what happened ?
    private Duration duration; // for how long (optional)?
    private String location; // where did this happen (optional)?
    private String partnerName; // at what partner did this happen (optional)?
    
    private Map<String,String> metadata = new HashMap<>(); // anyting else we need to know (optional) ?
    
}