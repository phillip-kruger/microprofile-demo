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
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Person POJO
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Entity(name = Person.NAME) @Table(name = Person.NAME)
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
@Schema(name="Person", description="POJO that represents a person.")
public class Person implements Serializable {
    private static final long serialVersionUID = -8531040143398373846L;
    
    @Id @Column(name = "ID",length = 100)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "NAME",length = 100)
    @ElementCollection(fetch = FetchType.EAGER,targetClass=String.class)
    @Size(min=1,max=4, message = "Name can not be empty")
    @Schema(required = true, description = "The name(s) of this person", type = SchemaType.ARRAY)
    private List<String> names;
    
    @Column(name = "SURNAME",length = 100)
    @NotNull(message = "Surname can not be empty") 
    @Size(min=2, message = "Surname '${validatedValue}' is too short, minimum {min} characters")
    @Schema(required = true, description = "The surname of this person", type = SchemaType.STRING)
    private String surname;
 
    @Column(name = "EMAIL",unique = true,length = 100)
    @NotNull(message = "Email can not be empty") 
    @Email(message = "Invalid email '${validatedValue}'")
    @Schema(required = true, description = "The email address of this person", type = SchemaType.STRING)
    private String email;
    
    public void addName(String name){
        if(names==null)names = new LinkedList<>();
        names.add(name);
    }
    
    public static final String NAME = "PERSON";
}
