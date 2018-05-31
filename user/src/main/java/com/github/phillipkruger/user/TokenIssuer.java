package com.github.phillipkruger.user;

import com.github.phillipkruger.jwt.Payload;
import com.github.phillipkruger.jwt.Token;
import com.github.phillipkruger.jwt.TokenJsonWriter;
import java.time.Duration;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Creates and issue a token
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Dependent
@Log
public class TokenIssuer {
   
    @Inject
    private TokenJsonWriter tokenJsonWriter;
    
    public String issue(String username,List<String> roles){
        
        Payload p = new Payload();
        p.setUserPrincipal(username);
        p.setTokenIssuer(tokenIssuer);
        p.setSubject(tokenSubject);
        p.setExpireIn(Duration.ofMinutes(tokenExpireMinutes));
        p.addGroups(roles);
        
        for(String member : tokenExpireAudience){
            p.addAudienceMember(member);
        }
        
        Token t = new Token(p);
        
        log.severe("token = " + t);
        
        return tokenJsonWriter.toString(t);
    }
    
    @Inject @ConfigProperty(name = "jwt.token.issuer")
    private String tokenIssuer;
    
    @Inject @ConfigProperty(name = "jwt.token.subject")
    private String tokenSubject;
    
    @Inject @ConfigProperty(name = "jwt.token.expire.minutes",defaultValue = "30")
    private int tokenExpireMinutes;
    
    @Inject @ConfigProperty(name = "jwt.token.audience",defaultValue = "global")
    private String[] tokenExpireAudience;
}