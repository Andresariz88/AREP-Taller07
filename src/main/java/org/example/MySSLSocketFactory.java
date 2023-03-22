package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.*;

public class MySSLSocketFactory {
    public static SSLSocketFactory getSocketFactory() throws Exception {

        //File trustStoreFile = new File("certificados/webAppTrustStore.p12");
        //char[] trustStorePassword = "ecikeypair".toCharArray();

        File trustStoreFile = new File("certificados/webAppTrustStoreAWS.p12");
        char[] trustStorePassword = "apikeypairAWS".toCharArray();

        // Load the trust store, the default type is "pkcs12", the alternative is "jks"
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);

        // Get the singleton instance of the TrustManagerFactory
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());

        // Itit the TrustManagerFactory using the truststore object
        tmf.init(trustStore);

        //Print the trustManagers returned by the TMF
        //only for debugging
        for(TrustManager t: tmf.getTrustManagers()){
            System.out.println(t);
        }

        //Set the default global SSLContext so all the connections will use it
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        // Devolver el SSLSocketFactory configurado con el contexto SSL
        return sslContext.getSocketFactory();
    }
}
