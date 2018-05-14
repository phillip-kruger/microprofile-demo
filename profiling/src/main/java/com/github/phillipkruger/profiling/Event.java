package com.github.phillipkruger.profiling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Event POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(name="Event", description="POJO that represents an event.")
public class Event  {
    
    @NotNull @Schema(required = true, example = "1", description = "Who did it ?")
    private int userId;
    @NotNull @Schema(required = true, description = "When did this happen ?")
    private LocalDateTime timeOccured; 
    @NotNull @Schema(required = false, description = "When did we hear about it ?")
    private LocalDateTime timeReceived = LocalDateTime.now();
    @NotNull @Schema(required = true, example = "Gym", description = "What happened ?")
    private String eventName;
    @Schema(required = false, description = "For how long ?")
    private Duration duration;
    @Schema(required = false, example = "Johannesburg", description = "Where did this happen ?")
    private String location;
    @Schema(required = false, example = "Virgin Active", description = "At what partner did this happen ?")
    private String partnerName;
    @Schema(required = false, description = "Anyting else we need to know ?")
    private Map<String,String> metadata = new HashMap<>();
    
}