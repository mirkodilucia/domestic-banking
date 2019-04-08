package banking.middleware;

import javafx.beans.property.*;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class Carta {

    private SimpleStringProperty cliente;
    private SimpleStringProperty nome;
    private SimpleStringProperty iban;
    private SimpleStringProperty cifra;

    public Carta(String carta, String nome, String iban, String cifra) {

        this.cliente = new SimpleStringProperty(carta);
        this.nome = new SimpleStringProperty(nome);
        this.iban = new SimpleStringProperty(iban);
        this.cifra = new SimpleStringProperty(cifra);

    }

    public String getCard() { return this.cliente.get(); }
    public String getNome() { return this.nome.get(); }
    public String getIban() { return this.iban.get(); }
    public String getCifra() { return this.cifra.get(); }

}
