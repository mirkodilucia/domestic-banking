package banking.frontend;

import banking.middleware.Movimento;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;

import java.util.List;

/**
 * Created by mirkodilucia on 07/06/17.
 */
public class TabellaMovimentiCliente extends TableView<Movimento> {

    private final ObservableList<Movimento> movimentiCliente;

    public TabellaMovimentiCliente() {

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Movimento, Movimento> nomeMovimento = new TableColumn<>("Nome");
        TableColumn<Movimento, String> beneficiarioMovimento = new TableColumn<>("Beneficiario");
        TableColumn<Movimento, String> dataMovimento = new TableColumn<>("Data Esecuzione");
        TableColumn<Movimento, String> descrizioneMovimento = new TableColumn<>("Descrizione");
        TableColumn<Movimento, String> cifraMovimento = new TableColumn<>("Cifra");

        nomeMovimento.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        beneficiarioMovimento.setCellValueFactory(new PropertyValueFactory<>("Beneficiario"));
        dataMovimento.setCellValueFactory(new PropertyValueFactory<>("DataEsecuzione"));
        descrizioneMovimento.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        cifraMovimento.setCellValueFactory(new PropertyValueFactory<>("Cifra"));

        movimentiCliente = FXCollections.observableArrayList();
        setItems(movimentiCliente);

        this.getColumns().add(dataMovimento);
        this.getColumns().add(nomeMovimento);
        this.getColumns().add(beneficiarioMovimento);
        this.getColumns().add(descrizioneMovimento);
        this.getColumns().add(cifraMovimento);

    }

    public void aggiornaListaMovimenti(List<Movimento> listaMovimenti) {

        movimentiCliente.clear();
        movimentiCliente.addAll(listaMovimenti);

    }

    public void svuotaListaMovimenti() {
        movimentiCliente.clear();
    }


}