package org.example;

import static spark.Spark.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import java.io.*;
import javax.net.ssl.HttpsURLConnection;

public class WebApp {

    public static void main(String[] args) throws Exception {
         secure(getKeyStore(), getPasswordKeyStore(), null, null);

        port(getPort());
        get("/hello", (req, res) -> getHttps());
    }

    /**
     * Hace conexión HTTPS con un SSL context específico (webAppTrustStore.p12)
     * @return respuesta HTTPS del servidor
     * @throws Exception
     */
    public static String getHttps() throws Exception {
        String httpsURL = "https://localhost:5002";
        URL myURL = new URL(httpsURL);
        HttpsURLConnection conn = (HttpsURLConnection) myURL.openConnection();

        // Configurar el SSL Socket Factory
        // Esta configuración es necesaria si el certificado del servidor no es de confianza o autofirmado
        conn.setSSLSocketFactory(MySSLSocketFactory.getSocketFactory());

        // Leer la respuesta del servidor
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();

    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000; //returns default port if heroku-port isn't set (i.e. on localhost)
    }

    static String getKeyStore() {
        if (System.getenv("KEYSTORE") != null) {
            return System.getenv("KEYSTORE");
        }
        return "certificados/ecikeystore.p12";
    }

    static String getPasswordKeyStore() {
        if (System.getenv("KEYSTOREPW") != null) {
            return System.getenv("KEYSTOREPW");
        }
        return "123456";
    }
}

// keytool -genkeypair -alias ecikeypair -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ecikeystore.p12 -validity 3650