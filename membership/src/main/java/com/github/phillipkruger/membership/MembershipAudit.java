package com.github.phillipkruger.membership;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.ObservesAsync;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import lombok.extern.java.Log;

/**
 * Membership Audit. 
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@RequestScoped
public class MembershipAudit {
    
    public void onFireNotification(@ObservesAsync Membership membership) {
        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(membership);
        log.severe(result);
    }
}