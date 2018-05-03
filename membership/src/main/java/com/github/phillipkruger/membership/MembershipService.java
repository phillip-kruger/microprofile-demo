package com.github.phillipkruger.membership;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.enterprise.context.RequestScoped;
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
public class MembershipService {
    
    @PersistenceContext(name="MembershipDS")
    private EntityManager em;
    
    @POST
    @Counted(name = "Membership created",absolute = true,monotonic = true)
    public Membership createMembership(@NotNull Membership membership){
        membership = em.merge(membership);
        log.log(Level.INFO, "Created membership [{0}]", membership);
        return membership;    
    }
    
    @DELETE @Path("{id}")
    @Counted(name = "Membership deleted",absolute = true,monotonic = true)
    public Membership deleteMembership(@NotNull @PathParam(value = "id") int id){
        Membership membership = getMembership(id);
        if(membership!=null){
            em.remove(membership);
        }
        return membership;
    }
    
    @GET
    @Timed(name = "Memberships requests time",absolute = true,unit = MetricUnits.MICROSECONDS)
    public List<Membership> getAllMemberships() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        
        TypedQuery<Membership> query = em.createNamedQuery(Membership.QUERY_FIND_ALL, Membership.class);
        return query.getResultList();
    }
    
    @GET @Path("{id}")
    @Counted(name = "Membership requests",absolute = true,monotonic = true)
    public Membership getMembership(@NotNull @PathParam(value = "id") int id) {
        

        
        return em.find(Membership.class,id);
    }
}
