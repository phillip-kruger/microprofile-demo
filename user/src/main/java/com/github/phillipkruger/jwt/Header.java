package com.github.phillipkruger.jwt;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Header part of JWT
 * @author Phillip Kruger (phillip.kruger@phillip-kruger.com)
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class Header {
   
   // typ: This JOSE header parameter identifies the token format and must be "JWT"
   private final String type = "JWT";
   
   // alg: This JOSE header parameter identifies the cryptographic algorithm used to secure the JWT. 
   // MP-JWT requires the use of the RSASSA-PKCS1-v1_5 SHA-256 algorithm and must be specified as "RS256"
   private final String algorithm = "RS256";
   
   // kid: This JOSE header parameter is a hint indicating which key was used to secure the JWT. (eg. "abc-1234567890")
   private Optional<String> keyHint = Optional.empty();
   
}