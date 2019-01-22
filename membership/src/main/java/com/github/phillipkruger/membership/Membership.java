package com.github.phillipkruger.membership;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Membership POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Entity(name = Membership.NAME) @Table(name = Membership.NAME)
@NamedQueries({
    @NamedQuery(name = Membership.QUERY_FIND_ALL, query = "SELECT m FROM MEMBERSHIP m")
})
@Schema(name="Membership", description="POJO that represents a membership.")
public class Membership implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;

    public static final String QUERY_FIND_ALL = "Membership.findAll";
    
    @Id @Column(name = "ID", length = 100)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membershipId;
    
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNER_ID")
    @NotNull(message = "Owner can not be empty")
    @Schema(required = true, description = "Who's membership is this ?", implementation = Person.class)
    private Person owner;
    
    @Column(name = "TYPE")
    @NotNull(message = "Type can not be empty")
    @Schema(required = true, description = "What type of membership is this ?", enumeration = {"FREE","FULL"})
    private Type type;
    
    public Membership(Person owner){
        this.owner = owner;
        this.type = Type.FREE;
    }
    
    public Membership(Person owner, Type membershipType){
        this.owner = owner;
        this.type = membershipType;
    }
    
    public static final String NAME = "MEMBERSHIP";
}