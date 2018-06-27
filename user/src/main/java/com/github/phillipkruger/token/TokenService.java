package com.github.phillipkruger.token;

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
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

/**
 * Token Service. JAX-RS
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 * @see https://docs.payara.fish/documentation/microprofile/jwt.html
 * @see https://github.com/javaee-samples/microprofile1.2-samples/tree/master/jwt-auth
 * TODO: Add Metrics
 */
@Log
@RequestScoped
@Path("/token")
@Produces(MediaType.TEXT_PLAIN)
@Tag(name = "Token service",description = "JWT Issuer")
@DeclareRoles({"user", "admin"})
@DenyAll
public class TokenService {
    
    @Context
    private SecurityContext securityContext;
    
    @Inject
    private TokenIssuer tokenIssuer;
    
    @Inject
    private TokenSigner tokenSigner;
    
    @GET 
    @Path("/issue")
    @Operation(description = "Issue a JWT Token for the logged in user")
    @APIResponse(responseCode = "200", description = "Get the signed token")
    @RolesAllowed({"admin","user"})
    public Response issueToken(){
        
        Principal userPrincipal = securityContext.getUserPrincipal();
        String username = userPrincipal.getName();
        List<String> roles = getUserRoles();
        String token = tokenIssuer.issue(username, roles);
        String signToken = tokenSigner.signToken(token);
        
        return Response.ok(signToken).build();
    }
    
    private List<String> getUserRoles() {
        return Arrays.stream(TokenService.class.getAnnotation(DeclareRoles.class).value())
                .filter(roleName -> securityContext.isUserInRole(roleName))
                .collect(Collectors.toList());
    }                
                
}

//    @Counted(name = "Tokens issued", absolute = true, monotonic = true)