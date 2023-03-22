package org.example;


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

public class RemoteServer {

    public static void main(String... args){

        secure(getKeyStore(), getPasswordKeyStore(), null, null);

        port(getPort());
        //changeTruststore();
        get("/", (req, res) -> "Hello from Remote Server");
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5002; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    static String getKeyStore() {
        if (System.getenv("KEYSTORE") != null) {
            return System.getenv("KEYSTORE");
        }
        return "certificados/apikeypair.p12";
        //return "certificados/ecikeystore.p12";
    }

    static String getPasswordKeyStore() {
        if (System.getenv("KEYSTOREPW") != null) {
            return System.getenv("KEYSTOREPW");
        }
        return "apikeypair";
        //return "123456";
    }

}
