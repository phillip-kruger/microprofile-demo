package com.github.phillipkruger.membership.health;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

@Log
@Health
@ApplicationScoped
public class MembershipHealthCheck implements HealthCheck {

    @Inject @ConfigProperty(name = "health.membership.dbtimeout", defaultValue = "5")
    private int timeout;
    
    @Resource(name="com.github.phillipkruger.membership")
    private DataSource dataSource;
    
    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("membership");
        
        try {
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(timeout);
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            responseBuilder = responseBuilder
                    .withData("databaseProductName", metaData.getDatabaseProductName())
                    .withData("databaseProductVersion", metaData.getDatabaseProductVersion())
                    .withData("driverName", metaData.getDriverName())
                    .withData("driverVersion", metaData.getDriverVersion())
                    .withData("isValid", isValid);
            
            return responseBuilder.state(isValid).build();
            
        } catch (SQLException ex) {
            log.log(Level.SEVERE, null, ex);
            responseBuilder = responseBuilder
                    .withData("exceptionMessage", ex.getMessage());
            return responseBuilder.down().build();
        }

   }
}
