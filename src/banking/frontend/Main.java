package banking.frontend;

import banking.backend.*;
import banking.middleware.*;
import banking.middleware.exception.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.logging.LogManager;

public class Main extends Application {

    private static LocalDate dataInizio;
    private static LocalDate dataFine;

    private final static String NOME_APP = "BANKING";
    private static Cliente clienteAutenticato;
    private TabellaMovimentiCliente tabellaMovimentiCliente;

    private GraficoBarre graficoBarre;
    private GraficoTorta graficoTorta;

    private VBox vbox;
    private SelettoreCarte selettoreCarteInvio;
    private Label etichettaMessaggio;
    private Label etichettaSaldo;
    private String selectedIban;
    private SelettoreIntervalloDate selettoreDate;

    @Override
    public void start(Stage stage) throws Exception {

        Configurazione configurazione = GestoreConfigurazione.getConfiguratione();

        if (configurazione == null) {
            System.out.println("Errore durante la configurazione dell'applicazione");
            System.exit(-1);
        }

        LogClient.aperturaApplicazione(NOME_APP);

        try {
            clienteAutenticato = GestoreDatabase.autenticaCliente(configurazione.utenteAccesso, configurazione.passwordAccesso);
        } catch (ClienteNonTrovatoException e) {
            LogClient.inviaErroreApplicazione(NOME_APP, e.getMessage());
            System.out.println("Credenziali errate!");
            System.exit(-1);
        }

        tabellaMovimentiCliente = new TabellaMovimentiCliente();
        graficoBarre = new GraficoBarre(configurazione.modalitaGrafico, configurazione.intervalloGrafico);
        graficoTorta = new GraficoTorta();

        StackPane root = new StackPane();
        root.getChildren().add(generaInterfaccia());

        Scene scene = new Scene(root, 1280, 920);
        scene.getStylesheets().add("file:./res/stile.css");

        preparaChiusuraApplicazione(stage);

        stage.setTitle(NOME_APP);
        stage.setScene(scene);
        stage.show();

        stage.setResizable(false);

    }

    private VBox generaInterfaccia() {
        
        LocalDate today = LocalDate.now();
        selettoreDate = new SelettoreIntervalloDate(today.minus(30, ChronoUnit.DAYS), today);
        GridPane pane = selettoreDate.getSelettoreDate();

        etichettaMessaggio = new Label();
        etichettaMessaggio.setText("Benvenuto!");

        pane.add(etichettaMessaggio, 0, 2, 16, 1);

        Label etichettaCarte = new Label("Carta selezionata :");
        pane.add(etichettaCarte,2, 0, 1,1);

        SelettoreCarte selettoreCarte = new SelettoreCarte(true);
        pane.add(selettoreCarte,2, 1, 1,1);

        selettoreDate.setAscoltatoreCambioData(new SelettoreIntervalloDate.AscoltatoreCambiaData() {

            @Override
            public void cambioDataInizio() {
                LogClient.inviaEventoAzioneUtente(NOME_APP, "Cambio data iniziale");
            }

            @Override
            public void cambioDataFine() {
                LogClient.inviaEventoAzioneUtente(NOME_APP, "Cambio data finale");
            }

            @Override
            public void cambioIntervallo(LocalDate initialDate, LocalDate endDate) {
                Main.dataInizio = initialDate;
                Main.dataFine = endDate;

                LogClient.inviaEventoAzioneUtente(NOME_APP, "Cambio intervallo date" + initialDate + " -- " + endDate);
                tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail(), initialDate, endDate));
                graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), selectedIban, initialDate, endDate));
            }
        });

            GestoreDatabase.aggiornaSaldoCarte();
            selettoreDate.richiediAggiornamentoDate();

            List<Carta> listaCarte = GestoreDatabase.caricaCarteCliente(clienteAutenticato.getEmail());

            // Creazione del selettore di carte per l'invio di denaro
            selettoreCarteInvio = new SelettoreCarte(false);
            GridPane boxInviaDenaro = creaBoxInviaDenaro();
            selettoreCarteInvio.aggiornaChoicebox(listaCarte);

            // Aggiorno il selettore di carte impostando il primo valore inserito
            selettoreCarte.aggiornaChoicebox(listaCarte);
            selettoreCarte.getSelectionModel().selectFirst();
            selettoreCarte.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue observable, Object oldValue, Object newValue) -> {

                        if (clienteAutenticato != null) {
                            String[] cards = ((String) newValue)
                                    .replaceAll("\\s+", "")
                                    .split(":");

                            LogClient.inviaEventoAzioneUtente(NOME_APP, "Cambio carta di credito");
                            if (cards.length == 1 && cards[0].equals("Tutte")) {
                                selectedIban = null;
                                
                                tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail(), dataInizio, dataFine));
                                graficoBarre.aggiornaGrafico(clienteAutenticato.getEmail(), selectedIban);
                                graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), selectedIban, selettoreDate.getDataInizio(), selettoreDate.getDataFine()));
                                etichettaSaldo.setText("Saldo cliente: " + clienteAutenticato.getCifra());

                            } else {
                                selectedIban = cards[1];
                                
                                tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail(), cards[1], dataInizio, dataFine));
                                graficoBarre.aggiornaGrafico(clienteAutenticato.getEmail(), selectedIban);
                                String saldoCarta = GestoreDatabase.caricaSaldoCarta(clienteAutenticato.getEmail(), selectedIban);
                                graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), selectedIban, selettoreDate.getDataInizio(), selettoreDate.getDataFine()));
                                etichettaSaldo.setText("Saldo cliente: " + clienteAutenticato.getCifra() + ", Saldo carta : " + saldoCarta + " €");
                            }
                        }
                    });

        etichettaSaldo = new Label();
        etichettaSaldo.setText("Saldo cliente: " + clienteAutenticato.getCifra());

        // Aggiungo il messaggio di benvenuto all'utente appena autenticato
        etichettaMessaggio.setText("Bentornato, " + clienteAutenticato.getNome());

        tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail()));
        graficoBarre.aggiornaGrafico(clienteAutenticato.getEmail(), selectedIban);
        graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), null, selettoreDate.getDataInizio(), selettoreDate.getDataFine()));

        HBox hBox = new HBox();
        hBox.setHgrow(graficoBarre, Priority.ALWAYS);
        hBox.getChildren().addAll(graficoBarre, graficoTorta);

        vbox = new VBox();
        VBox.setVgrow(tabellaMovimentiCliente, Priority.ALWAYS);
        VBox.setVgrow(graficoBarre, Priority.ALWAYS);
        vbox.getChildren().addAll(pane, tabellaMovimentiCliente, etichettaSaldo, boxInviaDenaro, hBox);

        return vbox;
    }

    private void preparaChiusuraApplicazione(Stage stage) {

        stage.setOnCloseRequest( e-> {
            new GestoreCache().salvaCache();
            LogClient.chiusuraApplicazione(NOME_APP);
        });

    }

    private GridPane creaBoxInviaDenaro() {

        GridPane pane = new GridPane();
        pane.setHgap(6);
        pane.setVgap(4);
        pane.setPadding(new Insets(6, 4, 6, 4));

        selettoreCarteInvio = new SelettoreCarte(false);
        pane.add(selettoreCarteInvio, 0, 1, 1, 1);

        TextField cliente = new TextField("Cliente");
        pane.add(cliente, 1, 1, 1, 1);

        TextField iban = new TextField("Iban");
        pane.add(iban, 2, 1, 1,1);

        TextField quantita = new TextField("Quantita");
        pane.add(quantita, 3, 1, 1, 1);

        Button inviaDenaro = new Button("Invia Denaro");
        pane.add(inviaDenaro, 4, 1, 1, 1);

        inviaDenaro.setOnAction((ActionEvent ev) -> {
            LogClient.inviaEventoAzioneUtente(NOME_APP, "Invio denaro");
            try {
                GestoreDatabase.inviaDenaro(selettoreCarteInvio.getValue().toString(), clienteAutenticato.getEmail(), cliente.getText(), iban.getText(), Float.parseFloat(quantita.getText()));
                tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail()));
                graficoBarre.aggiornaGrafico(clienteAutenticato.getEmail(), selectedIban);

                graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), selectedIban, selettoreDate.getDataInizio(), selettoreDate.getDataFine()));

            } catch (ClienteNonTrovatoException e) {
                etichettaMessaggio.setText(e.getMessage());
                LogClient.inviaErroreApplicazione(NOME_APP, e.getMessage());
            } catch (SaldoNonSufficienteException e) {
                etichettaMessaggio.setText(e.getMessage());
                LogClient.inviaErroreApplicazione(NOME_APP, e.getMessage());
            }
        });

        Button revocaMovimento = new Button("Revoca");
        LogClient.inviaEventoAzioneUtente(NOME_APP, "Revoca transazione");
        revocaMovimento.setOnAction((ActionEvent ev) -> {
            Movimento movimento = tabellaMovimentiCliente.getSelectionModel().getSelectedItem();
            if (movimento != null) {
                LogClient.inviaEventoAzioneUtente(NOME_APP, "Revoca");
                try {
                    GestoreDatabase.revocaTrasferimento(movimento.getIban(), movimento.getDataEsecuzione());
                    tabellaMovimentiCliente.aggiornaListaMovimenti(GestoreDatabase.caricaMovimentiCliente(clienteAutenticato.getEmail()));
                    graficoBarre.aggiornaGrafico(clienteAutenticato.getEmail(), selectedIban);
                    graficoTorta.aggiornaGrafico(GestoreDatabase.ottieniCategorizzazioneMovimenti(clienteAutenticato.getEmail(), selectedIban, selettoreDate.getDataInizio(), selettoreDate.getDataFine()));

                } catch (IntervalloSuperatoException e) {
                    etichettaMessaggio.setText(e.getMessage());
                }

            }else{
                etichettaMessaggio.setText("Selezione un movimento prima");
            }

        });
        pane.add(revocaMovimento,5,1,1,1);

        return pane;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
