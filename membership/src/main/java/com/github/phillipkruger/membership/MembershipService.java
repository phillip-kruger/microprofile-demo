package com.github.phillipkruger.membership;

import java.util.List;
import java.util.logging.Level;
import javax.ejb.Stateless;
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

@Log
@Stateless
@Path("/")
@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
public class MembershipService {
    
    @PersistenceContext(name="com.github.phillipkruger.membership")
    private EntityManager em;
    
    @POST
    public Membership createMembership(@NotNull Membership membership){
        membership = em.merge(membership);
        log.log(Level.INFO, "Created membership [{0}]", membership);
        return membership;    
    }
    
    @DELETE @Path("{id}")
    public Membership deleteMembership(@NotNull @PathParam(value = "id") int id){
        Membership membership = getMembership(id);
        if(membership!=null){
            em.remove(membership);
        }
        return membership;
    }
    
    @GET
    public List<Membership> getAllMemberships() {
        TypedQuery<Membership> query = em.createNamedQuery(Membership.QUERY_FIND_ALL, Membership.class);
        return query.getResultList();
    }
    
    @GET @Path("{id}")
    public Membership getMembership(@NotNull @PathParam(value = "id") int id) {
        return em.find(Membership.class,id);
    }
}
