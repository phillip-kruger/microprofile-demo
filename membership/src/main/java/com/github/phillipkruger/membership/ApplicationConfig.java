package com.github.phillipkruger.membership;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Activate JAX-RS. 
 * All REST Endpoints available under /api
 * 
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@ApplicationPath("/api")
public class ApplicationConfig extends Application {

}
