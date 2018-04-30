package com.github.phillipkruger.membership.startup;

import com.github.phillipkruger.membership.Membership;
import com.github.phillipkruger.membership.MembershipService;
import com.github.phillipkruger.membership.Person;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

@Startup
@Singleton
public class BootstrapData {
    
    @Inject
    private MembershipService membershipService;
    
    @PostConstruct
    public void populateDemoData(){
        membershipService.createMembership(createMembership("Kruger","Natus","Phillip"));
        membershipService.createMembership(createMembership("Kruger","Charmaine","Juliet"));
        membershipService.createMembership(createMembership("van der Westhuizer","Minki"));
        membershipService.createMembership(createMembership("Torvalds","Linus","Benedict"));
    }
    
    private Membership createMembership(String surname, String... name){
        Person owner = new Person();
        owner.setNames(Arrays.asList(name));
        owner.setSurname(surname);
        return new Membership(owner);
    }
    
}
