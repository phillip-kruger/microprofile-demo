package com.github.phillipkruger.membership;

import java.security.Principal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.security.DeclareRoles;
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
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import lombok.extern.java.Log;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

/**
 * Membership Service. JAX-RS
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@RequestScoped
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@DeclareRoles({"user", "admin"})
public class MembershipService {
    
    @PersistenceContext(name="MembershipDS")
    private EntityManager em;
    
    @Context
    private SecurityContext securityContext;
    
    @Inject 
    private JsonWebToken jsonWebToken; 
    
    @Inject
    @Claim("group")
    private Set<String> groups;
    
    @POST
    @Counted(name = "Membership created",absolute = true,monotonic = true)
    @RolesAllowed({"admin"})
    public Membership createMembership(@NotNull Membership membership){
        membership = em.merge(membership);
        log.log(Level.INFO, "Created membership [{0}]", membership);
        return membership;    
    }
    
    @DELETE @Path("{id}")
    @Counted(name = "Membership deleted",absolute = true,monotonic = true)
    @RolesAllowed({"admin"})
    public Membership deleteMembership(@NotNull @PathParam(value = "id") int id){
        Membership membership = getMembership(id);
        if(membership!=null){
            em.remove(membership);
        }
        return membership;
    }
    
    @GET
    @Timed(name = "Memberships requests time",absolute = true,unit = MetricUnits.MICROSECONDS)
    @Timeout(value = 5 , unit = ChronoUnit.SECONDS)
    @CircuitBreaker(failOn = RuntimeException.class,requestVolumeThreshold = 1, failureRatio=1, delay = 10, delayUnit = ChronoUnit.SECONDS )
    @RolesAllowed({"admin"})
    public List<Membership> getAllMemberships() {
        TypedQuery<Membership> query = em.createNamedQuery(Membership.QUERY_FIND_ALL, Membership.class);
        return query.getResultList();
    }
    
    @GET @Path("{id}")
    @Counted(name = "Membership requests",absolute = true,monotonic = true)
    @Timeout(value = 5 , unit = ChronoUnit.SECONDS)
    @CircuitBreaker(failOn = RuntimeException.class,requestVolumeThreshold = 1, failureRatio=1, delay = 10, delayUnit = ChronoUnit.SECONDS )
    @RolesAllowed({"admin", "user"})
    public Membership getMembership(@NotNull @PathParam(value = "id") int id) {
        
        Principal userPrincipal = securityContext.getUserPrincipal();
        log.severe(">>>>>> User principle: " + userPrincipal.getName());
        
        log.severe(">>>>>> JWT Issuer: " + jsonWebToken.getIssuer());
        log.severe(">>>>>> JWT Name: " + jsonWebToken.getName());
        log.severe(">>>>>> JWT Subject: " + jsonWebToken.getSubject());
        log.severe(">>>>>> JWT ID: " + jsonWebToken.getTokenID());
        log.severe(">>>>>> JWT Groups: " + jsonWebToken.getGroups());
        
        Membership m = em.find(Membership.class,id);
        
        if(m==null || securityContext.isUserInRole("admin") || m.getOwner().getEmail().equals(userPrincipal.getName()))return m;
        throw new NotAuthorizedException("User [" + userPrincipal.getName() + "] not authorized to request membership for [" + id + "]");
    }
}