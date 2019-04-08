package banking.frontend;

import banking.backend.GestoreDatabase;
import banking.middleware.DatoGrafico;
import javafx.collections.*;
import javafx.scene.chart.*;

import java.util.List;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class GraficoBarre extends BarChart {

    private final ObservableList<DatoGrafico> movimentiCliente;
    public int modalita;
    public int intervallo;

    public GraficoBarre(int modalita, int intervallo) {
        super(new CategoryAxis(),(new NumberAxis()));

        this.modalita = modalita;
        this.intervallo = intervallo;

        setLegendVisible(false);
        setAnimated(false);

        setTitle("Totale movimenti per ogni mese");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Totale movimenti per ogni mese");

        movimentiCliente = FXCollections.observableArrayList();
        setData(movimentiCliente);
    }

    public void aggiornaGrafico(String email, String iban) {
        List<DatoGrafico> dati = null;

        System.out.println(modalita + "|" + email + "//" + intervallo);

        switch (modalita) {
            case 0:
                dati = GestoreDatabase.ottieniMovimentiQuotidiani(email, iban, intervallo);
                break;
            case 1:
                dati = GestoreDatabase.ottieniMovimentiSettimanali(email, iban, intervallo);
                break;
            case 2:
                dati = GestoreDatabase.ottieniMovimentiMensili(email, iban, intervallo);
                break;
        }

        Series<String, Double> movimenti = new Series<>();

        for (int i=0; i<dati.size(); i++) {
            movimenti.getData().add(new XYChart.Data(dati.get(i).getEtichetta(), dati.get(i).getValore()));
        }

        this.setData(FXCollections.observableArrayList(movimenti));

    }

    public void svuotaGrafico() {
        this.setData(null);
    }

}
