package com.github.phillipkruger.jwt;

import com.nimbusds.jose.JOSEException;
import static com.nimbusds.jose.JOSEObjectType.JWT;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import static com.nimbusds.jwt.JWTClaimsSet.parse;
import com.nimbusds.jwt.SignedJWT;
import java.io.IOException;
import static java.lang.Thread.currentThread;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import javax.enterprise.context.Dependent;
import lombok.extern.java.Log;

@Log
@Dependent
public class TokenSigner {
    
    public String signToken(String jwtJson){
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader
                    .Builder(RS256)
                    .keyID("/privateKey.pem")
                    .type(JWT)
                    .build(), parse(jwtJson));

            signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));

            return signedJWT.serialize();
        } catch (ParseException | IOException | NoSuchAlgorithmException | InvalidKeySpecException | JOSEException ex) {
            throw new RuntimeException("Error while trying to sign token", ex);
        }
    }
    
    private PrivateKey readPrivateKey(String resourceName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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