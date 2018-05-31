package com.github.phillipkruger.jwt;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import lombok.extern.java.Log;
import org.eclipse.microprofile.jwt.Claims;

@Log
@Dependent
public class TokenJsonWriter {
    
    public String toString(Token token){
        return toJsonObject(token).toString();
    }
    
    public JsonObject toJsonObject(Token token){
        JsonObjectBuilder jtoken = Json.createObjectBuilder();
        
        JsonObject header = getHeader(token.getHeader());
        log.severe("> header " + header.toString());
        jtoken.add(HEADER,header);
        
        JsonObject payload = getPayload(token.getPayload());
        log.severe("> payload " + payload.toString());
        jtoken.add(PAYLOAD,payload);
        return jtoken.build();
    }
    
    private JsonObject getHeader(Header header){
        JsonObjectBuilder jheader = Json.createObjectBuilder();
        
        jheader.add(HEADER_TYPE, header.getType());
        jheader.add(HEADER_ALGORITHM, header.getAlgorithm());
        if(header.getKeyHint().isPresent()){
            jheader.add(Claims.kid.name(), header.getKeyHint().get());
        }
        return jheader.build();
    }
    
    private JsonObject getPayload(Payload payload){
        JsonObjectBuilder jpayload = Json.createObjectBuilder();
        jpayload.add(Claims.jti.name(), payload.getTokenId());
        jpayload.add(Claims.iss.name(), payload.getTokenIssuer());
        jpayload.add(Claims.aud.name(), toJsonArrayBuilder(payload.getAudience()));
        jpayload.add(Claims.exp.name(), payload.getExpirationTimeAsLong());
        jpayload.add(Claims.iat.name(), payload.getIssueTimeAsLong());
        jpayload.add(Claims.sub.name(), payload.getSubject());
        jpayload.add(Claims.upn.name(), payload.getUserPrincipal());
        jpayload.add(Claims.groups.name(), toJsonArrayBuilder(payload.getGroups()));
        
        if(payload.hasCustomClaims()){
            Set<Map.Entry<String, String>> customClaimsEntrySet = payload.getCustomClaims().entrySet();
            for(Map.Entry<String, String> cc:customClaimsEntrySet){
                jpayload.add(cc.getKey(), cc.getValue());
            }
        }
        
        return jpayload.build();
    }
    
    private JsonArrayBuilder toJsonArrayBuilder(List<String> list){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(String l:list){
            arrayBuilder.add(l);
        }
        return arrayBuilder;
    }
    
    private static final String HEADER = "header";
    private static final String PAYLOAD = "payload";
    
    private static final String HEADER_TYPE = "typ";
    private static final String HEADER_ALGORITHM = "alg";
   
}