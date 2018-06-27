package com.github.phillipkruger.token;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Payload part of JWT
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class Token {
    
    // jti: Provides a unique identifier for the JWT 
    private String tokenId = UUID.randomUUID().toString();
    
    // iss: The token issuer (eg. https://server.example.com)
    private String tokenIssuer;
    
    // aud: Identifies the recipients that the JWT is intended for (system user ? eg, app, web etc)
    private final List<String> audience = new ArrayList<>();
    
    // exp: Identifies the expiration time on or after which the JWT MUST NOT be accepted for processing
    private LocalDateTime expirationTime;
    
    // iat: Identifies the time at which the issuer generated the JWT
    private LocalDateTime issueTime = LocalDateTime.now();
    
    // sub: Identifies the principal that is the subject of the JWT. See the "upn" claim for how this relates to the runtime java.security.Principal
    // This is the token issuing IDP subject
    private String subject;
    
    // upn: Provides the user principal name in the java.security.Principal interface
    private String userPrincipal;
    
    // groups: Provides the list of group names that have been assigned to the principal of the MP-JWT. 
    // This typically will require a mapping at the application container level to application deployment roles, 
    // but a one-to-one between group names and application role names is required to be performed in addition to any other mapping.
    private final List<String> groups = new ArrayList<>();
    
    // The JWT can contain any number of other custom claims,
    private Map<String,String> customClaims = new HashMap<>();
    
    public void addAudienceMember(String audienceMember){
        this.audience.add(audienceMember);
    }

    public void addGroup(String group){
        this.groups.add(group);
    }
    
    public void addGroups(List<String> groups){
        this.groups.addAll(groups);
    }
    
    public void addCustom(String key,String value){
        this.customClaims.put(key, value);
    }
    
    public boolean hasCustomClaims(){
        return this.customClaims!=null && !this.customClaims.isEmpty();
    }
    
    public Long getIssueTimeAsLong(){
        return issueTime.toEpochSecond(SYSTEM_OFFSET);
    }
    
    public Long getExpirationTimeAsLong(){
        return expirationTime.toEpochSecond(SYSTEM_OFFSET);
    }
    
    public void setExpireIn(Duration duration){
        this.expirationTime = issueTime.plus(duration);
    }
    
    private static final ZoneOffset SYSTEM_OFFSET = OffsetDateTime.now().getOffset();
}