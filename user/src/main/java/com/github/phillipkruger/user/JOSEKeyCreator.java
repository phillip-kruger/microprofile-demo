package com.github.phillipkruger.user;

import static com.nimbusds.jose.JOSEObjectType.JWT;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static com.nimbusds.jwt.JWTClaimsSet.parse;
import static java.lang.Thread.currentThread;
import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.eclipse.microprofile.jwt.Claims;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class JOSEKeyCreator {
   
    public static String generateJWTString() {
        try {
            
            //JSON
            
            
            String jwttoken = "{"
                    + "\"iss\": \"fish.payara.example\","
                    + "\"jti\": \"a-123\","
                    + "\"sub\": \"24400320\","
                    + "\"aud\": \"s6BhdRkqt3\","
                    + "\"upn\": \"test\","
                    + "\"groups\": ["
                    + "\"architect\","
                    + "\"master\","
                    + "\"leader\","
                    + "\"dev\""
                    + "]"
                    + "}";

            JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
            JSONObject jwtJson = (JSONObject) parser.parse(jwttoken);

            long currentTimeInSecs = (System.currentTimeMillis() / 1000);
            long expirationTime = currentTimeInSecs + 1000;

            jwtJson.put(Claims.iat.name(), currentTimeInSecs);
            jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
            jwtJson.put(Claims.exp.name(), expirationTime);

            SignedJWT signedJWT = new SignedJWT(new JWSHeader
                    .Builder(RS256)
                    .keyID("/privateKey.pem")
                    .type(JWT)
                    .build(), parse(jwtJson));

            signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));

            return signedJWT.serialize();
        } catch (Exception ex) {
            throw new RuntimeException("Error creating a token", ex);
        }
    }

    private static PrivateKey readPrivateKey(String resourceName) throws Exception {
        byte[] byteBuffer = new byte[16384];
        int length = currentThread().getContextClassLoader()
                                    .getResource(resourceName)
                                    .openStream()
                                    .read(byteBuffer);

        String key = new String(byteBuffer, 0, length).replaceAll("-----BEGIN (.*)-----", "")
                                                      .replaceAll("-----END (.*)----", "")
                                                      .replaceAll("\r\n", "")
                                                      .replaceAll("\n", "")
                                                      .trim();

        return KeyFactory.getInstance("RSA")
                         .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
    }
}
