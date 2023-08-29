package com.programming.techie.springredditclone.security;


import com.programming.techie.springredditclone.exceptions.SpringRedditException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.parser;

@Service
@RequiredArgsConstructor
public class JwtProvider {
    private KeyStore keyStore;

    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        try {
            keyStore=KeyStore.getInstance("JKS");
            InputStream resourceAsStream=getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream,"secret".toCharArray());

        }
        catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringRedditException("Exception While Loading KeyStore");
        }
    }
     public String generateToken(Authentication authentication) {
         org.springframework.security.core.userdetails.User principal= (User) authentication.getPrincipal();
         return Jwts.builder()
                 .setSubject(principal.getUsername())
                 .signWith(getPrivateKey())
                 .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                 .compact();
     }
    public String generateTokenWithUserName(String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .signWith(getPrivateKey())
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey() {
         try {
             return (PrivateKey) keyStore.getKey("springblog","secret".toCharArray());
         }
         catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
             throw new SpringRedditException("Exception occured While Retrieving the Public Key");
         }
    }

    public boolean validateToken(String jwt) {

        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() {

        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        }
        catch (KeyStoreException  e) {
            throw new SpringRedditException("Exception occured While Retrieving the Public Key From Key Store");
        }

    }

    public String getUserNameFromToken(String token) {
        Claims claims= parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }
    public Long getJwtExpirationInMillis() {
        return jwtExpirationInMillis;
    }

}
