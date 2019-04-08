package banking.frontend;

import banking.middleware.Carta;
import javafx.collections.*;
import javafx.scene.control.*;

import java.util.List;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class SelettoreCarte extends ChoiceBox {

    private final ObservableList<String> listaCarteCliente;
    private boolean mostraTutte;


    public SelettoreCarte(boolean mostraTutte) {

        this.mostraTutte = mostraTutte;

        listaCarteCliente = FXCollections.observableArrayList();
    }

    public void aggiornaChoicebox(List<Carta> listaCarte) {

        listaCarteCliente.clear();

        if (mostraTutte) { listaCarteCliente.add("Tutte"); }
        for (int i=0; i<listaCarte.size(); i++) {
            listaCarteCliente.add(listaCarte.get(i).getNome() + " : " + listaCarte.get(i).getIban());
        }

        this.setItems(listaCarteCliente);
        this.getSelectionModel().selectFirst();
    }

    public void svuotaSelettoreCarte() {
        listaCarteCliente.clear();
    }
}