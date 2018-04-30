package com.github.phillipkruger.membership;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Entity
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name")
    @ElementCollection(fetch = FetchType.EAGER,targetClass=String.class)
    @Size(min=1,max=4, message = "Name can not be empty")
    private List<String> names;
    
    @NotNull(message = "Surname can not be empty") 
    @Size(min=2, message = "Surname '${validatedValue}' is too short, minimum {min} characters")
    private String surname;
 
    public void addName(String name){
        if(names==null)names = new LinkedList<>();
        names.add(name);
    }
}
