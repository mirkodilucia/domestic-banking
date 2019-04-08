package banking.middleware;

import javafx.beans.property.*;

/**
 * Created by mirkodilucia on 07/06/17.
 */
public class Cliente {

    private final SimpleStringProperty email;
    private final SimpleStringProperty cognome;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty cifra;

    public Cliente(String email, String cognome, String nome, String cifra) {

        this.email = new SimpleStringProperty(email);
        this.cognome = new SimpleStringProperty(cognome);
        this.nome = new SimpleStringProperty(nome);
        this.cifra = new SimpleStringProperty(cifra);

    }

    public String getEmail() { return email.get(); }
    public String getCognome() { return cognome.get(); }
    public String getNome() { return nome.get(); }
    public String getCifra() { return cifra.get(); }

}
