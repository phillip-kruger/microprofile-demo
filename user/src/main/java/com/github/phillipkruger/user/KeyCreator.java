package com.github.phillipkruger.user;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.json.stream.JsonParser;
import org.eclipse.microprofile.jwt.Claims;

public class KeyCreator {
   
    public static String generateJWTString(String jsonResource) throws Exception {
        byte[] byteBuffer = new byte[16384];
        Thread.currentThread().getContextClassLoader()
                       .getResource(jsonResource)
                       .openStream()
                       .read(byteBuffer);

//        JsonParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
//        JSONObject jwtJson = (JSONObject) parser.parse(byteBuffer);
//
//        long currentTimeInSecs = (System.currentTimeMillis() / 1000);
//        long expirationTime = currentTimeInSecs + 1000;
//
//        jwtJson.put(Claims.iat.name(), currentTimeInSecs);
//        jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
//        jwtJson.put(Claims.exp.name(), expirationTime);
//
//        SignedJWT signedJWT = new SignedJWT(new JWSHeader
//                                            .Builder(RS256)
//                                            .keyID("/privateKey.pem")
//                                            .type(JWT)
//                                            .build(), parse(jwtJson));
//
//        signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));
//
//        return signedJWT.serialize();
        return null;
    }

    public static PrivateKey readPrivateKey(String resourceName) throws Exception {
        byte[] byteBuffer = new byte[16384];
        int length = Thread.currentThread().getContextClassLoader()
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
