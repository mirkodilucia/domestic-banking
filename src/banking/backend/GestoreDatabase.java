package banking.backend;

import banking.middleware.*;
import banking.middleware.exception.*;

import java.sql.*;
import java.text.*;
import java.time.*;
import java.util.*;

/**
 * Created by mirkodilucia on 07/06/17.
 */
public class GestoreDatabase {

    private static Connection connessioneDatabase;
    private final static String INDIRIZZO_DB;
    private final static String USERNAME_DB;
    private final static String PASSWORD_DB;

    private static final String queryAutenticazione = "SELECT * FROM cliente WHERE Email = ? AND Password = ?";
    private static final String querySelezionaMovimentiCliente = "SELECT * FROM movimento WHERE Email = ?";
    private static final String querySelezionaMovimentiClienteIntervallo = "SELECT * FROM movimento WHERE Email = ? AND DataEsecuzione BETWEEN DATE(?) AND DATE(?)";

    private static final String querySelezionaMovimentiCartaClienteIntervallo = "SELECT * FROM movimento WHERE Email = ? AND Iban = ? AND DataEsecuzione BETWEEN DATE(?) AND DATE(?)";

    private static final String queryCategorizzazioneIban = "SELECT Categoria, SUM(ABS(Cifra)) AS Cifra FROM Movimento WHERE Email = ? AND Iban = ? AND DataEsecuzione BETWEEN DATE(?) AND DATE(?) GROUP BY Categoria";
    private static final String queryCategorizzazione = "SELECT Categoria, SUM(ABS(Cifra)) AS Cifra FROM Movimento WHERE Email = ? AND DataEsecuzione BETWEEN DATE(?) AND DATE(?) GROUP BY Categoria";

    static {
        Configurazione configuration = GestoreConfigurazione.getConfiguratione();

        INDIRIZZO_DB = "jdbc:mysql://" +configuration.hostnameDatabase + ":" + configuration.portaDatabase + "/banca";
        USERNAME_DB = configuration.utenteDatabase;
        PASSWORD_DB = configuration.passwordDatabase;
    }

    public static Cliente autenticaCliente(String mail, String password) throws ClienteNonTrovatoException {

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementAutenticazione = connessioneDatabase.prepareStatement(queryAutenticazione);

            statementAutenticazione.setString(1, mail);
            statementAutenticazione.setString(2, password);

            ResultSet rs = statementAutenticazione.executeQuery();
            if (rs.next()) {
                return new Cliente(rs.getString("Email"),
                        rs.getString("Cognome"),
                        rs.getString("Nome"),
                        rs.getFloat("Saldo") + " €");
            }else{
                throw new ClienteNonTrovatoException("Credenziali non valide");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Movimento> caricaMovimentiCliente(String mail) {

        List<Movimento> listaMovimento = new ArrayList<>();
        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementClientMovement = connessioneDatabase.prepareStatement(querySelezionaMovimentiCliente);

            statementClientMovement.setString(1, mail);
            ResultSet rs = statementClientMovement.executeQuery();

            while (rs.next()) {

                listaMovimento.add(new Movimento(rs.getTimestamp("DataEsecuzione").toString(),
                        rs.getString("Email"),
                        rs.getString("Iban"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione"),
                        rs.getString("Beneficiario"),
                        rs.getFloat("Cifra") + " €"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return listaMovimento;

    }

    public static List<Movimento> caricaMovimentiCliente(String mail, LocalDate dataInizio, LocalDate dataFine) {

        List<Movimento> listaMovimenti = new ArrayList<>();
        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementMovimentiClienteIntervallo = connessioneDatabase.prepareStatement(querySelezionaMovimentiClienteIntervallo);

            statementMovimentiClienteIntervallo.setString(1, mail);
            statementMovimentiClienteIntervallo.setString(2, dataInizio.toString());
            statementMovimentiClienteIntervallo.setString(3, dataFine.toString());

            ResultSet rs = statementMovimentiClienteIntervallo.executeQuery();

            while (rs.next()) {

                java.util.Date dataEsecuzione = new java.util.Date(rs.getTimestamp("DataEsecuzione").getTime());
                Format formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                listaMovimenti.add(new Movimento(formato.format(dataEsecuzione),
                        rs.getString("Email"),
                        rs.getString("Iban"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione"),
                        rs.getString("Beneficiario"),
                        rs.getFloat("Cifra") + " €"));

            }

            return listaMovimenti;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static List<Movimento> caricaMovimentiCliente(String mail, String iban, LocalDate dataInizio, LocalDate dataFine) {

        List<Movimento> listaMovimenti = new ArrayList<>();
        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementMovimentiClienteIntervallo = connessioneDatabase.prepareStatement(querySelezionaMovimentiCartaClienteIntervallo);

            statementMovimentiClienteIntervallo.setString(1, mail);
            statementMovimentiClienteIntervallo.setString(2, iban);
            statementMovimentiClienteIntervallo.setString(3, dataInizio.toString());
            statementMovimentiClienteIntervallo.setString(4, dataFine.toString());

            ResultSet rs = statementMovimentiClienteIntervallo.executeQuery();

            while (rs.next()) {

                java.util.Date dataEsecuzione = new java.util.Date(rs.getTimestamp("DataEsecuzione").getTime());
                Format formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                listaMovimenti.add(new Movimento(formato.format(dataEsecuzione),
                        rs.getString("Email"),
                        rs.getString("Iban"),
                        rs.getString("Nome"),
                        rs.getString("Descrizione"),
                        rs.getString("Beneficiario"),
                        rs.getFloat("Cifra") + " €"));

            }

            return listaMovimenti;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static List<DatoGrafico> ottieniMovimentiMensili(String email, String iban, int intervallo) {

        final String queryOttieniSaldoDisponibileCliente = "SELECT DATE_FORMAT(DataEsecuzione, '%m-%Y') AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(DATE_FORMAT(DataEsecuzione, '%m-%Y'))";
        final String queryOttieniSaldoDisponibileCartaCliente = "SELECT DATE_FORMAT(DataEsecuzione, '%m-%Y') AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND Iban = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(DATE_FORMAT(DataEsecuzione, '%m-%Y'))";

        List<DatoGrafico> listaMovimentiMensili = new ArrayList<>();

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementMovimentiMensili;

            if (iban != null) {
                statementMovimentiMensili = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCartaCliente);

                statementMovimentiMensili.setString(1, email);
                statementMovimentiMensili.setString(2, iban);
                statementMovimentiMensili.setInt(3, intervallo);

            }else{
                statementMovimentiMensili = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCliente);

                statementMovimentiMensili.setString(1, email);
                statementMovimentiMensili.setInt(2, intervallo);

            }

            ResultSet rs = statementMovimentiMensili.executeQuery();

            while (rs.next()) {
                listaMovimentiMensili.add(new DatoGrafico(rs.getString("Mese"), rs.getFloat("Cifra")));
            }

        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listaMovimentiMensili;
    }

    public static List<DatoGrafico> ottieniMovimentiQuotidiani(String email, String iban, int intervallo) {

        final String queryOttieniSaldoDisponibileCliente = "SELECT DATE_FORMAT(DataEsecuzione, '%d-%m-%Y') AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(DATE_FORMAT(DataEsecuzione, '%d-%m-%Y'))";
        final String queryOttieniSaldoDisponibileCartaCliente = "SELECT DATE_FORMAT(DataEsecuzione, '%d-%m-%Y') AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND Iban = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(DATE_FORMAT(DataEsecuzione, '%d-%m-%Y'))";

        List<DatoGrafico> listaMovimentiMensili = new ArrayList<>();

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementMovimentiQuotidiani;

            if (iban != null) {
                statementMovimentiQuotidiani = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCartaCliente);

                statementMovimentiQuotidiani.setString(1, email);
                statementMovimentiQuotidiani.setString(2, iban);
                statementMovimentiQuotidiani.setInt(3, intervallo);

            }else{
                statementMovimentiQuotidiani = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCliente);

                statementMovimentiQuotidiani.setString(1, email);
                statementMovimentiQuotidiani.setInt(2, intervallo);

            }

            ResultSet rs = statementMovimentiQuotidiani.executeQuery();

            while (rs.next()) {
                listaMovimentiMensili.add(new DatoGrafico(rs.getString("Mese"), rs.getFloat("Cifra")));
            }

        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listaMovimentiMensili;
    }

    public static List<DatoGrafico> ottieniMovimentiSettimanali(String email, String iban, int intervallo) {

        final String queryOttieniSaldoDisponibileCliente = "SELECT YEARWEEK(DataEsecuzione) AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(YEARWEEK(DataEsecuzione))";
        final String queryOttieniSaldoDisponibileCartaCliente = "SELECT YEARWEEK(DataEsecuzione) AS Mese, SUM(Cifra) AS Cifra FROM Movimento WHERE Email = ? AND Iban = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL ? MONTH AND NOW()  GROUP BY(YEARWEEK(DataEsecuzione))";

        List<DatoGrafico> listaMovimentiMensili = new ArrayList<>();

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementMovimentiSettimanali;

            if (iban != null) {
                statementMovimentiSettimanali = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCartaCliente);

                statementMovimentiSettimanali.setString(1, email);
                statementMovimentiSettimanali.setString(2, iban);
                statementMovimentiSettimanali.setInt(3, intervallo);

            }else{
                statementMovimentiSettimanali = connessioneDatabase.prepareStatement(queryOttieniSaldoDisponibileCliente);

                statementMovimentiSettimanali.setString(1, email);
                statementMovimentiSettimanali.setInt(2, intervallo);

            }

            ResultSet rs = statementMovimentiSettimanali.executeQuery();

            while (rs.next()) {
                listaMovimentiMensili.add(new DatoGrafico(rs.getString("Mese"), rs.getFloat("Cifra")));
            }

        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listaMovimentiMensili;

    }

    public static List<DatoGrafico> ottieniCategorizzazioneMovimenti(String email, String iban, LocalDate dataInizio, LocalDate dataFine) {

        List<DatoGrafico> listaCategorizzazioneMovimenti = new ArrayList<>();

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);

            PreparedStatement statementCategorizzazione;
            if (iban != null) {
                statementCategorizzazione = connessioneDatabase.prepareStatement(queryCategorizzazioneIban);

                statementCategorizzazione.setString(1, email);
                statementCategorizzazione.setString(2, iban);
                statementCategorizzazione.setString(3, dataInizio.toString());
                statementCategorizzazione.setString(4, dataFine.toString());
            }else{
                statementCategorizzazione = connessioneDatabase.prepareStatement(queryCategorizzazione);

                statementCategorizzazione.setString(1, email);
                statementCategorizzazione.setString(2, dataInizio.toString());
                statementCategorizzazione.setString(3, dataFine.toString());
            }

            ResultSet rs = statementCategorizzazione.executeQuery();

            while (rs.next()) {
                listaCategorizzazioneMovimenti.add(new DatoGrafico(rs.getString("Categoria"), rs.getFloat("Cifra")));
            }

            return listaCategorizzazioneMovimenti;
        }catch (SQLException ex) {
            ex.printStackTrace();
        }

        return listaCategorizzazioneMovimenti;
    }

    public static List<Carta> caricaCarteCliente(String mail) {

        List<Carta> cartas = new ArrayList<>();
        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementCartaCliente = connessioneDatabase.prepareStatement("SELECT * FROM carta WHERE Email = ?");

            statementCartaCliente.setString(1, mail);

            ResultSet rs = statementCartaCliente.executeQuery();

            while (rs.next()) {
                cartas.add(new Carta(rs.getString("Email"),
                        rs.getString("Nome"),
                        rs.getString("Iban"),
                        rs.getFloat("Cifra") + " €"));
            }

            return cartas;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void inviaDenaro(String cartaSelezionata, String cliente, String beneficiario, String cartaBeneficiario, float quantita) throws ClienteNonTrovatoException, SaldoNonSufficienteException {

        PreparedStatement statementSelezionaBeneficiario = null;
        PreparedStatement statementControllaSaldoCarta = null;
        PreparedStatement aggiornaMovimentiCliente = null;
        PreparedStatement aggiornaMovimentiBeneficiario = null;

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            connessioneDatabase.setAutoCommit(false);

            String[] cartaSel = (cartaSelezionata)
                    .replaceAll("\\s+","")
                    .split(":");

            // Controllo se esiste il beneficiario e la sua carta
            statementSelezionaBeneficiario = connessioneDatabase.prepareStatement("SELECT * FROM cliente cl INNER JOIN carta ca ON cl.Email = ca.Email WHERE cl.Email = ? AND ca.Iban = ?");
            statementSelezionaBeneficiario.setString(1, cliente);
            statementSelezionaBeneficiario.setString(2, cartaSel[1]);

            ResultSet rsBeneficiario = statementSelezionaBeneficiario.executeQuery();
            rsBeneficiario.last();
            if (rsBeneficiario.getRow() != 1) {
                connessioneDatabase.rollback();
                throw new ClienteNonTrovatoException("Utente non trovato");
            }

            // Controllo se la carta ha abbastanza denaro
            statementControllaSaldoCarta = connessioneDatabase.prepareStatement("SELECT * FROM carta WHERE Iban = ? AND Email = ? AND Cifra > ?");
            statementControllaSaldoCarta.setString(1, cartaSel[1]);
            statementControllaSaldoCarta.setString(2, cliente);
            statementControllaSaldoCarta.setFloat(3, quantita);

            ResultSet rsSaldoSufficiente = statementSelezionaBeneficiario.executeQuery();
            rsSaldoSufficiente.last();
            if (rsSaldoSufficiente.getRow() != 1) {
                connessioneDatabase.rollback();
                throw new SaldoNonSufficienteException("Saldo sulla carta non sufficiente");
            }

            // Inserisco il movimento per chi invia il denaro
            aggiornaMovimentiCliente = connessioneDatabase.prepareStatement("INSERT INTO movimento (Email, Iban, DataEsecuzione, Beneficiario, Nome, Categoria, Descrizione, Cifra) VALUES (?, ?, CURRENT_TIMESTAMP, ?, \"Bonifico bancario\", \"Trasferimento\", ?, ?)");
            aggiornaMovimentiCliente.setString(1, cliente);
            aggiornaMovimentiCliente.setString(2, cartaSel[1]);
            aggiornaMovimentiCliente.setString(3, beneficiario);
            aggiornaMovimentiCliente.setString(4, "Bonifico bancario a favore di " + beneficiario);
            aggiornaMovimentiCliente.setFloat(5, (-1) * quantita); // quantità negativa per chi invia il denaro
            aggiornaMovimentiCliente.execute();

            // Inserisco i movimenti per chi riceve il denaro
            aggiornaMovimentiBeneficiario = connessioneDatabase.prepareStatement("INSERT INTO movimento (Email, Iban, DataEsecuzione, Beneficiario, Nome, Categoria, Descrizione, Cifra) VALUES (?, ?, CURRENT_TIMESTAMP, ?, \"Bonifico bancario\", \"Trasferimento\", ?, ?)");
            aggiornaMovimentiBeneficiario.setString(1, beneficiario);
            aggiornaMovimentiBeneficiario.setString(2, cartaBeneficiario);
            aggiornaMovimentiBeneficiario.setString(3, cliente);
            aggiornaMovimentiBeneficiario.setString(4, "Bonifico bancario da parte di " + cliente);
            aggiornaMovimentiBeneficiario.setFloat(5, quantita);
            aggiornaMovimentiBeneficiario.execute();

            connessioneDatabase.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                statementSelezionaBeneficiario.close();
                statementControllaSaldoCarta.close();
                aggiornaMovimentiCliente.close();
                aggiornaMovimentiBeneficiario.close();

                connessioneDatabase.setAutoCommit(true);
                aggiornaSaldoCarte();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void revocaTrasferimento(String iban, String dataEsecuzione) throws IntervalloSuperatoException {

        PreparedStatement statementControllaData = null;
        PreparedStatement statementRimuoviTrasferimentoCliente = null;

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            connessioneDatabase.setAutoCommit(false);

            // Controllo contemporaneamente se non sono passate 6 ore dal movimento e che il movimento corrisponde ad una transazione tra utenti
            statementControllaData = connessioneDatabase.prepareStatement("SELECT * FROM movimento WHERE DataEsecuzione = ? AND DataEsecuzione BETWEEN NOW() - INTERVAL 6 HOUR AND NOW()");
            statementControllaData.setString(1, dataEsecuzione);
            //statementControllaData.setString(2, dataEsecuzione);
            ResultSet rs = statementControllaData.executeQuery();

            rs.last();
            if (rs.getRow() != 2) {
                throw new IntervalloSuperatoException("Il movimento selezionato è stato fatto più di 6 ore fa o non corrisponde ad una transazione interna");
            }

            statementRimuoviTrasferimentoCliente = connessioneDatabase.prepareStatement("DELETE FROM movimento WHERE DataEsecuzione = ?");
            statementRimuoviTrasferimentoCliente.setString(1, dataEsecuzione);
            statementRimuoviTrasferimentoCliente.executeUpdate();

            connessioneDatabase.commit();

            connessioneDatabase.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();

            try {

                statementControllaData.close();
                statementRimuoviTrasferimentoCliente.close();

                connessioneDatabase.setAutoCommit(true);

                aggiornaSaldoCarte();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

    }

    public static void aggiornaSaldoCarte() {

        PreparedStatement statementAggiornaSaldoCarta = null;
        PreparedStatement statementAggiornaSaldoCliente = null;

        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            connessioneDatabase.setAutoCommit(false);

            statementAggiornaSaldoCarta = connessioneDatabase.prepareStatement("UPDATE carta c SET Cifra = (SELECT SUM(Cifra) FROM movimento WHERE Email = c.Email)");
            statementAggiornaSaldoCarta.execute();

            statementAggiornaSaldoCliente = connessioneDatabase.prepareStatement("UPDATE cliente c SET Saldo = (SELECT IF (SUM(Cifra) IS NULL, 0, SUM(Cifra)) FROM carta WHERE Email = c.Email GROUP BY Email)");
            statementAggiornaSaldoCliente.execute();

            connessioneDatabase.commit();

        } catch (SQLException e) {
            e.printStackTrace();

            try {
                statementAggiornaSaldoCarta.close();
                statementAggiornaSaldoCliente.close();

                connessioneDatabase.setAutoCommit(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static String caricaSaldoCarta(String email, String carta) {
        try {
            connessioneDatabase = DriverManager.getConnection(INDIRIZZO_DB, USERNAME_DB, PASSWORD_DB);
            PreparedStatement statementSaldoCarta = connessioneDatabase.prepareStatement("SELECT Cifra FROM carta WHERE Email = ? AND Iban = ?");

            statementSaldoCarta.setString(1, email);
            statementSaldoCarta.setString(2, carta);

            ResultSet rs = statementSaldoCarta.executeQuery();

            while (rs.next()) {
                return (rs.getString("Cifra") == null)? "0" :rs.getString("Cifra");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }
}
