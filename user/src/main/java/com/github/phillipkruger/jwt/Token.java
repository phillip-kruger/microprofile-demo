package com.github.phillipkruger.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT POJO. Consist of Header, Payload and Signature
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class Token {
    private Header header = new Header();
    private Payload payload;
    private Signature signature;
    
    public Token(Payload payload){
        this.payload = payload;
    }
}