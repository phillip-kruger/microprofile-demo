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
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Membership POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Entity(name = Membership.NAME) @Table(name = Membership.NAME)
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = Membership.QUERY_FIND_ALL, query = "SELECT m FROM MEMBERSHIP m")
})
public class Membership implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;

    public static final String QUERY_FIND_ALL = "Membership.findAll";
    
    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membershipId;
    
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "OWNER_ID")
    @NotNull(message = "Owner can not be empty")
    private Person owner;
    
    @Column(name = "TYPE")
    @NotNull(message = "Type can not be empty")
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