package banking.backend;

import banking.middleware.GestoreXML;

import java.io.*;
import java.net.*;
import java.nio.file.*;

/**
 * Created by banking on 14/05/17.
 */
public class LogServer{

    private Integer portaServer;

    private LogServer(Integer portaServer) {
        this.portaServer = portaServer;
    }

    private void appendiFile(String xml) {
        GestoreXML xmlManager = new GestoreXML(null, "./log/evento.xsd");

        if (!xmlManager.validaXML(xml)) {
            return;
        }

        String ls = System.lineSeparator();
        xml+=ls+ls;

        try {
            if (!Files.exists(Paths.get("./log/log.xml")))
                Files.createFile(Paths.get("./log/log.xml"));

            Files.write(Paths.get("./log/log.xml"), xml.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Impossibile scrivere sul file di log" + e.getMessage());
        }

    }

    private void start() {
        while (true) {
            System.out.println("In attesa di connessioni...");
            try (
                    ServerSocket serverSocket = new ServerSocket(portaServer);
                    Socket socket = serverSocket.accept();
                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream())) {

                String event = dataInputStream.readUTF();
                System.out.println("Ricevuto: " + event);
                appendiFile(event);

            } catch (IOException e) {
                System.out.println("Errore server di log: " + e.getMessage());
                return;
            }
        }
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Porta non acquisita");
            System.exit(-1);
        }

        System.out.println("Porta acquisita" + args[0]);
        LogServer logServer = new LogServer(Integer.parseInt(args[0]));
        logServer.start();
    }
}
