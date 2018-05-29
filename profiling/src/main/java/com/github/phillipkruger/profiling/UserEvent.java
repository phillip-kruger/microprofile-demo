package com.github.phillipkruger.profiling;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Event POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Schema(name="UserEvent", description="POJO that represents an event.")
public class UserEvent  {
    
    @NotNull @Schema(required = true, description = "Who did it ?",type = SchemaType.INTEGER)
    private int userId;
    
    @NotNull @Schema(required = true, description = "When did this happen ?")
    private Date timeOccured; 
        
    @NotNull @Schema(required = false, description = "When did we hear about it ?")
    private Date timeReceived = new Date();
    
    @NotNull @Schema(required = true, description = "What happened ?")
    private String eventName;
    
    @Schema(required = false, description = "For how long ?",type = SchemaType.INTEGER)
    private int durationInMinutes;
    
    @Schema(required = false, description = "Where did this happen ?")
    private String location;
    
    @Schema(required = false, description = "At what partner did this happen ?")
    private String partnerName;
    
    @Schema(required = false, description = "Anyting else we need to know ?")
    private Map<String,String> metadata = new HashMap<>();
    
    public void addMetadata(String key,String val){
        this.metadata.put(key, val);
    }
    
    
}