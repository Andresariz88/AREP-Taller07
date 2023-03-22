package org.example;

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
    }

    static String getPasswordKeyStore() {
        if (System.getenv("KEYSTOREPW") != null) {
            return System.getenv("KEYSTOREPW");
        }
        return "apikeypair";
    }

}
