package com.github.phillipkruger.profiling.membership;

import com.github.phillipkruger.microprofileextentions.restclient.RuntimeResponseExceptionMapper;
import javax.enterprise.context.Dependent;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Dependent
@RegisterRestClient

@Consumes(MediaType.APPLICATION_JSON) @Produces(MediaType.APPLICATION_JSON)
@RegisterProvider(RuntimeResponseExceptionMapper.class)
@Path("/api")
public interface MembershipProxy {

    @GET @Path("/{id}") @Operation(hidden = true)
    public Membership getMembership(@HeaderParam("Authorization") String authorization, @NotNull @PathParam(value = "id") int id);
 }

