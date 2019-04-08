package banking.middleware;

import com.thoughtworks.xstream.XStream;

import java.io.*;
import java.net.*;

/**
 * Created by banking on 12/05/17.
 */
public class LogClient extends Thread  {

    private final Evento evento;
    private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String IP_ADDRESS = "0.0.0.0";
    private String stringaXml;

    private LogClient(Evento evento) {

        this.evento = evento;

    }

    private String serializzaEvento() {
        XStream xstream = new XStream();
        xstream.alias("Evento", Evento.class);
        xstream.useAttributeFor(Evento.class,"nomeApp");

        stringaXml = XML_HEADER + System.lineSeparator()+ xstream.toXML(evento);

        return stringaXml;
    }

    @Override
    public void run() {
        String serverLogAddess = GestoreConfigurazione.getConfiguratione().indirizzoServerLog;
        Integer serverLogPort = GestoreConfigurazione.getConfiguratione().portaServerLog;

        try (Socket socket = new Socket(serverLogAddess, serverLogPort);

             DataOutputStream dos = new DataOutputStream(socket.getOutputStream());){
            dos.writeUTF(serializzaEvento());

        } catch (UnknownHostException e) {
                 System.out.println("Errore durante l'invio del log: " + e.getLocalizedMessage());
        } catch (IOException e) {
                 System.out.println("Errore di I/O: " + e.getLocalizedMessage());
        }

    }

    private static void inviaLogEvento(String nomeApp, String componente, String t) {

        Evento a = new Evento(nomeApp, componente, t, System.currentTimeMillis());
        (new LogClient(a)).start();
    }

    public static void inviaEventoAzioneUtente(String nomeApp, String azione) {

        Evento a = new Evento(nomeApp, azione, IP_ADDRESS, System.currentTimeMillis());

        (new LogClient(a)).start();

    }

    public static void inviaErroreApplicazione(String nomeApp, String errore) {

        Evento a = new Evento(nomeApp, errore, IP_ADDRESS, System.currentTimeMillis());

        (new LogClient(a)).start();
    }

    public static void aperturaApplicazione(String nomeApp) {

        Evento e = new Evento(nomeApp, "AVVIO APPLICAZIONE", IP_ADDRESS, System.currentTimeMillis());

        (new LogClient(e)).start();

    }

    public static void chiusuraApplicazione(String nomeApp) {

        Evento e = new Evento(nomeApp, "CHIUSURA APPLICAZIONE", IP_ADDRESS, System.currentTimeMillis());

        (new LogClient(e)).start();

    }
}
