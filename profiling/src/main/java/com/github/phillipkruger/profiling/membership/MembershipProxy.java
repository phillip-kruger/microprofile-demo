package com.github.phillipkruger.profiling.membership;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/")
public interface MembershipProxy {

    @Operation(hidden = true)
    @GET @Path("{id}")
    public Membership getMembership(@NotNull @PathParam(value = "id") int id);
     
 }