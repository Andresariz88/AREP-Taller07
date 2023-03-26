package org.example;

import static spark.Spark.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.DatatypeConverter;

public class WebApp {

    private static Map<String, String> users = new HashMap<>();

    public static void main(String[] args) throws Exception {
        secure(getKeyStore(), getPasswordKeyStore(), null, null);
        addUsers();

        staticFileLocation("/");
        port(getPort());
        get("/", (req, res) -> getHttps());

        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (users.containsKey(username)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(password.getBytes());
                byte[] digest = md.digest();
                String myHash = DatatypeConverter
                        .printHexBinary(digest).toUpperCase();
                if (users.get(username).equals(myHash)) {
                    req.session().attribute("username", username);
                    res.redirect("/");
                }
            } else {
                return "Credenciales incorrectas, intente de nuevo";
            }
            return null;
        });

        before((req, res) -> {
            String username = req.session().attribute("username");
            if (username == null) {
                res.redirect("/login.html");
            }
        });
    }

    /**
     * Hace conexión HTTPS con un SSL context específico (webAppTrustStore.p12)
     * @return respuesta HTTPS del servidor
     * @throws Exception
     */
    public static String getHttps() throws Exception {
        String httpsURL = "https://ec2-54-82-213-170.compute-1.amazonaws.com:5002";
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

    private static void addUsers() throws NoSuchAlgorithmException {
        String user = "user";
        String password = "password";

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        String myHash = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        users.put(user, myHash);

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
        //return "certificados/ecikeystore.p12";
        return "certificados/webkeypairAWS.p12";
    }

    static String getPasswordKeyStore() {
        if (System.getenv("KEYSTOREPW") != null) {
            return System.getenv("KEYSTOREPW");
        }
        //return "123456";
        return "webkeypairAWS";

    }
}

// keytool -genkeypair -alias ecikeypair -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore ecikeystore.p12 -validity 3650