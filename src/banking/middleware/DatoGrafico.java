package banking.middleware;

import javafx.beans.property.*;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class DatoGrafico {

    private final SimpleStringProperty etichetta;
    private final SimpleFloatProperty valore;

    public DatoGrafico(String month, float amount) {

        this.etichetta = new SimpleStringProperty(month);
        this.valore = new SimpleFloatProperty(amount);

    }

    public String getEtichetta() { return this.etichetta.get(); }
    public float getValore() { return this.valore.get(); }

}