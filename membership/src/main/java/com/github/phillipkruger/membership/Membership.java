package com.github.phillipkruger.membership;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
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
@Entity
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = Membership.QUERY_FIND_ALL, query = "SELECT m FROM Membership m"),
    @NamedQuery(name = Membership.QUERY_FIND_ALL_TYPE, query = "SELECT m FROM Membership m WHERE m.type=:type")
})
public class Membership implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;

    public static final String QUERY_FIND_ALL = "Membership.findAll";
    public static final String QUERY_FIND_ALL_TYPE = "Membership.findAllType";
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membershipId;
    
    @OneToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "owner_id")
    @NotNull(message = "Owner can not be empty")
    private Person owner;
    
    @NotNull(message = "Type can not be empty")
    private Type type;
    
}