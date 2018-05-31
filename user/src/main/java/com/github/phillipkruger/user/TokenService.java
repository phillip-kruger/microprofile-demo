package com.github.phillipkruger.user;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.java.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import static com.github.phillipkruger.user.SecurityRoles.ADMIN_ROLE;
import static com.github.phillipkruger.user.SecurityRoles.USER_ROLE;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 * Token Service. JAX-RS
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * @see https://docs.payara.fish/documentation/microprofile/jwt.html
 * @see https://github.com/javaee-samples/microprofile1.2-samples/tree/master/jwt-auth
 */
@DeclareRoles({USER_ROLE,ADMIN_ROLE})
@Log
@RequestScoped
@Path("/token")
@Produces(MediaType.TEXT_PLAIN)
@Tag(name = "Token service",description = "JWT Issuer")
public class TokenService {
    
    @Context
    private SecurityContext securityContext;
    
    @Inject
    private TokenIssuer tokenIssuer;
    
    @GET
    @Operation(description = "Ping to test")
    @APIResponse(responseCode = "200", description = "Received the ping, return a pong")
    @RolesAllowed({ "admin", "user" })
    public Response ping(){
        
        String authenticationScheme = securityContext.getAuthenticationScheme();
        log.severe(">> authenticationScheme = " + authenticationScheme);
        
        boolean secure = securityContext.isSecure();
        log.severe(">> secure = " + secure);
        
        boolean isAdmin = securityContext.isUserInRole(ADMIN_ROLE);
        log.severe(">> isAdmin = " + isAdmin);
        
        boolean isUser = securityContext.isUserInRole(USER_ROLE);
        log.severe(">> isUser = " + isUser);
        
        Principal userPrincipal = securityContext.getUserPrincipal();
        String username = userPrincipal.getName();
        log.severe(">> username = " + username);
        
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("ping", "pong");
        
        return Response.ok(b.build().toString()).build();
    }
    
    @GET @Path("/issue")
    @Operation(description = "Issue a JWT Token for the logged in user")
    @APIResponse(responseCode = "200", description = "Get the signed token")
    @RolesAllowed({ "admin", "user" })
    public Response issueToken(){
        
        //return Response.ok(JOSEKeyCreator.generateJWTString()).build();
        
        // TODO: Check if there is a valid token ?
        
        Principal userPrincipal = securityContext.getUserPrincipal();
        String username = userPrincipal.getName();
        log.severe(">> username = " + username);
        List<String> roles = getUserRoles();
        log.severe(">> roles = " + roles);
        String token = tokenIssuer.issue(username, roles);
        log.severe(">> token = " + token);
        return Response.ok(token).build();
    }
    
    private List<String> getUserRoles() {
        return Arrays.stream(TokenService.class.getAnnotation(DeclareRoles.class).value())
                .filter(roleName -> securityContext.isUserInRole(roleName))
                .collect(Collectors.toList());
    }                
                
}
