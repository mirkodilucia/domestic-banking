package banking.middleware;

import java.io.Serializable;

/**
 * Created by banking on 12/05/17.
 */
public class Evento implements Serializable {

    public final String nomeApp;
    public final String tag;
    public String clientIP;
    public long timestamp;

    public Evento(String nomeApp, String tag, String clientIP, long timestamp) {

        this.nomeApp = nomeApp;
        this.tag = tag;
        this.clientIP = clientIP;
        this.timestamp = timestamp;

    }

}
