package com.github.phillipkruger.membership.health;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

@Stateless
public class DatasourceProducer {
    
    @Resource(lookup = "java:global/membership/MembershipDS")
    private DataSource dataSource;
    
    @Produces
    public DataSource getDataSource(){
        return this.dataSource;
    }
}
