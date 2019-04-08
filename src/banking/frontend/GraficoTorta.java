package banking.frontend;

import banking.middleware.DatoGrafico;
import javafx.collections.*;
import javafx.scene.chart.*;

import java.util.*;

/**
 * Created by mirkodilucia on 17/06/17.
 */
public class GraficoTorta extends PieChart {

    private final ObservableList<PieChart.Data> categorieSpesa;

    public GraficoTorta () {

        setLegendVisible(false);
        setAnimated(false);

        setTitle("Categorizzazione movimenti");

        categorieSpesa = FXCollections.observableArrayList(
                        new PieChart.Data("Grapefruit", 13),
                        new PieChart.Data("Oranges", 25),
                        new PieChart.Data("Plums", 10),
                        new PieChart.Data("Pears", 22),
                        new PieChart.Data("Apples", 30));

        setData(categorieSpesa);

    }

    public void aggiornaGrafico(List<DatoGrafico> spese) {
        categorieSpesa.clear();

        for (int i=0; i< spese.size(); i++) {
            this.categorieSpesa.add(new PieChart.Data(spese.get(i).getEtichetta(),
                                                    spese.get(i).getValore()));
        }

        setData(categorieSpesa);
    }

    public  void svuotaGrafico() {
        this.setData(null);
    }
}
