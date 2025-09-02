package org.oauth.common.util;

import org.oauth.common.data.SERVICE_RESPONSE;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static org.oauth.common.data.ConstParam.RSA;

/**
 * packageName    : org.oauth.common.util
 * fileName       : PemKeyUtil
 * author         : sinuk
 * date           : 2025-09-02
 * description    : RSA PEM Key 유틸 클래스
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-09-02         sinuk       최초 생성
 **/

public class PemKeyUtil {

    private PemKeyUtil() {}

    public static RSAPrivateKey readPrivateKey(Path pemPath) {
        try {
            String pem = Files.readString(pemPath)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(SERVICE_RESPONSE.PEM_KEY_INVALID.getMessage(), e);
        }
    }

    public static RSAPublicKey readPublicKey(Path pemPath) {
        try {
            String pem = Files.readString(pemPath)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(pem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(RSA);
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(SERVICE_RESPONSE.PEM_KEY_INVALID.getMessage(), e);
        }
    }

    public static RSAPublicKey loadPublic(String location) {
        try (InputStream in = openStream(location)) {
            byte[] bytes = in.readAllBytes();
            String pem = new String(bytes).replaceAll("-----\\w+ PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = java.util.Base64.getDecoder().decode(pem);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(der);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (Exception e) { throw new IllegalStateException(SERVICE_RESPONSE.PEM_KEY_INVALID.getMessage() + location, e); }
    }

    public static RSAPrivateKey loadPrivate(String location) {
        try (InputStream in = openStream(location)) {
            byte[] bytes = in.readAllBytes();
            String pem = new String(bytes).replaceAll("-----\\w+ PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] der = java.util.Base64.getDecoder().decode(pem);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) { throw new IllegalStateException(SERVICE_RESPONSE.PEM_KEY_INVALID.getMessage() + location, e); }
    }

    private static InputStream openStream(String location) throws FileNotFoundException {
        String path = location.substring("classpath:".length());
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(path.startsWith("/") ? path.substring(1) : path);
        if (in == null) throw new java.io.FileNotFoundException(SERVICE_RESPONSE.FILE_NOT_FOUND.getMessage() + path);
        return in;

    }
}