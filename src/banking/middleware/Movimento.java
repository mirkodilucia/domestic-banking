package banking.middleware;

import javafx.beans.property.*;

/**
 * Created by mirkodilucia on 07/06/17.
 */
public class  Movimento {

    private final SimpleStringProperty dataEsecuzione;

    private final SimpleStringProperty email;
    private final SimpleStringProperty iban;
    private final SimpleStringProperty nome;
    private final SimpleStringProperty descrizione;
    private final SimpleStringProperty cifra;
    private final SimpleStringProperty beneficiario;


    public Movimento(String dataEsecuzione, String email, String iban, String nome, String descrizione, String beneficiario, String cifra) {

        this.dataEsecuzione = new SimpleStringProperty(dataEsecuzione);

        this.email = new SimpleStringProperty(email);
        this.iban = new SimpleStringProperty(iban);
        this.nome = new SimpleStringProperty(nome);
        this.descrizione = new SimpleStringProperty(descrizione);

        this.cifra = new SimpleStringProperty(cifra);

        this.beneficiario = new SimpleStringProperty(beneficiario);
    }

    public String getDataEsecuzione() {return dataEsecuzione.get(); }
    public String getIban() { return iban.get(); }
    public String getEmail() {return email.get(); }
    public String getNome() { return nome.get(); }
    public String getDescrizione() { return descrizione.get(); }
    public String getBeneficiario() { return beneficiario.get(); }
    public String getCifra() { return cifra.get(); }

}