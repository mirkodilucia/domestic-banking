package banking.middleware;

import java.io.Serializable;

/**
 * Created by banking on 12/05/17.
 */
public class Configurazione implements Serializable {

    public Integer portaServerLog;
    public String indirizzoServerLog;

    public String indirizzoClient;

    public String hostnameDatabase;
    public Integer portaDatabase;

    public String utenteDatabase;
    public String passwordDatabase;

    public String utenteAccesso;
    public String passwordAccesso;

    public Integer modalitaGrafico;
    public Integer intervalloGrafico;

}
