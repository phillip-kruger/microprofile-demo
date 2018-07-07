package com.github.phillipkruger.membership;

import com.github.phillipkruger.microprofileextentions.jwt.UserAccess;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.opentracing.Traced;

/**
 * Membership Service. JAX-RS
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Membership service", description = "Managing the membership")
public class MembershipService {
    
    @PersistenceContext(name="MembershipDS")
    private EntityManager em;
    
    // To activate: curl -X PUT "http://localhost:8080/membership/api/config/key/activateBadCode" -H  "accept: */*" -H  "Content-Type: text/plain" -d "true"
    @Inject @ConfigProperty(name = "activateBadCode",defaultValue = "false")
    private boolean activateBadCode;
    
    @GET
    @Timed(name = "Memberships requests time",absolute = true,unit = MetricUnits.MICROSECONDS)
    @Operation(description = "Get all the current memberships")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successfull, returning memberships"),
            @APIResponse(responseCode = "504", description = "Service timed out"),
            @APIResponse(responseCode = "401", description = "User not authorized")
    })
    @RolesAllowed({"admin"})
    @SecurityRequirement(name = "Authorization")
    @Timeout(value = 3 , unit = ChronoUnit.SECONDS)
    @CircuitBreaker(failOn = RuntimeException.class,requestVolumeThreshold = 1, failureRatio=1, delay = 10, delayUnit = ChronoUnit.SECONDS )
    public List<Membership> getAllMemberships() {
        
        // Some bad code went into production...
        if(activateBadCode){
            try {
                log.severe("Sleeping for 10 seconds...");
                TimeUnit.SECONDS.sleep(10L);
            } catch (InterruptedException ex) {
                log.severe(ex.getMessage());
            }
        }
        
        TypedQuery<Membership> query = em.createNamedQuery(Membership.QUERY_FIND_ALL, Membership.class);
        return query.getResultList();
    }
    
    @GET 
    @Path("{id}")
    @Counted(name = "Membership requests",absolute = true,monotonic = true)
    @Operation(description = "Get a certain Membership by id")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Successfull, returning membership", 
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = Membership.class))),
            @APIResponse(responseCode = "504", description = "Service timed out"),
            @APIResponse(responseCode = "401", description = "User not authorized")
    })
    @SecurityRequirement(name = "Authorization")
    @RolesAllowed({"admin","user"})
    @UserAccess(pathToUserName = "owner.email" , ignoreGroups = {"admin"})
    @Traced(operationName = "GetMembershipById", value = true)
    @CircuitBreaker(failOn = RuntimeException.class,requestVolumeThreshold = 1, failureRatio=1, 
            delay = 10, delayUnit = ChronoUnit.SECONDS ) 
    @Timeout(value = 3 , unit = ChronoUnit.SECONDS)
    @Bulkhead(2)
    public Membership getMembership(@NotNull @PathParam(value = "id") int id) {
        // Some bad code went into production...
        if(activateBadCode){
            try {
                log.severe("Sleeping for 10 seconds...");
                TimeUnit.SECONDS.sleep(10L);
            } catch (InterruptedException ex) {
                log.severe(ex.getMessage());
            }
        }
        
        return em.find(Membership.class,id);
    }

    @POST
    @Counted(name = "Membership created",absolute = true,monotonic = true)
    @Operation(description = "Create a new Membership")
    @APIResponse(responseCode = "200", description = "Successful, returning the created membership")
    public Membership createMembership(@NotNull @RequestBody(description = "The membership",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,schema = @Schema(implementation = Membership.class)))
            Membership membership){
        membership = em.merge(membership);
        log.log(Level.INFO, "Created membership [{0}]", membership);
        return membership;    
    }
    
    @DELETE 
    @Path("{id}")
    @Counted(name = "Membership deleted",absolute = true,monotonic = true)
    @RolesAllowed({"admin"})
    public Membership deleteMembership(@NotNull @PathParam(value = "id") int id){
        Membership membership = em.find(Membership.class,id);
        if(membership!=null){
            em.remove(membership);
        }
        return membership;
    }
}