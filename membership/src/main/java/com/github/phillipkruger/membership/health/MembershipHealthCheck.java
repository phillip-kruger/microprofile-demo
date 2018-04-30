package com.github.phillipkruger.membership.health;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

/**
 * Simple health check that test that the DB is available.
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Log
@Health
@ApplicationScoped
public class MembershipHealthCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        Connection connection = null;
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("membership");
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection( connectionUrl, dbUser, dbPassword);
            
            boolean isValid = connection.isValid(timeout);
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            responseBuilder = responseBuilder
                    .withData("databaseProductName", metaData.getDatabaseProductName())
                    .withData("databaseProductVersion", metaData.getDatabaseProductVersion())
                    .withData("driverName", metaData.getDriverName())
                    .withData("driverVersion", metaData.getDriverVersion())
                    .withData("isValid", isValid);
            
            return responseBuilder.state(isValid).build();
            
            
        } catch(SQLException | ClassNotFoundException e) {
            log.log(Level.SEVERE, null, e);
            responseBuilder = responseBuilder
                    .withData("exceptionMessage", e.getMessage());
            return responseBuilder.down().build();
        } finally {
            if(connection!=null)try {
                connection.close();
            } catch (SQLException ex) {
                log.log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Inject @ConfigProperty(name = "health.membership.dbtimeout", defaultValue = "5")
    private int timeout;
    
    @Inject @ConfigProperty(name = "health.membership.driverClassName", defaultValue = "org.h2.jdbcx.JdbcDataSource")
    private String driverClassName;
    
    @Inject @ConfigProperty(name = "health.membership.connectionUrl", defaultValue = "jdbc:h2:mem:membership;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
    private String connectionUrl;
        
    @Inject @ConfigProperty(name = "health.membership.dbUser", defaultValue = "sa")
    private String dbUser;
    
    @Inject @ConfigProperty(name = "health.membership.dbPassword", defaultValue = "sa")
    private String dbPassword;
    
}
