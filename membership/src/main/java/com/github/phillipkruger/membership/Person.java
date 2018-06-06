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
import javax.persistence.Table;
import javax.validation.constraints.Email;
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
@Entity(name = Person.NAME) @Table(name = Person.NAME)
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;
    
    @Id @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "NAME")
    @ElementCollection(fetch = FetchType.EAGER,targetClass=String.class)
    @Size(min=1,max=4, message = "Name can not be empty")
    private List<String> names;
    
    @Column(name = "SURNAME")
    @NotNull(message = "Surname can not be empty") 
    @Size(min=2, message = "Surname '${validatedValue}' is too short, minimum {min} characters")
    private String surname;
 
    @Column(name = "EMAIL",unique = true)
    @NotNull(message = "Email can not be empty") 
    @Email(message = "Invalid email '${validatedValue}'")
    private String email;
    
    public void addName(String name){
        if(names==null)names = new LinkedList<>();
        names.add(name);
    }
    
    public static final String NAME = "PERSON";
}
