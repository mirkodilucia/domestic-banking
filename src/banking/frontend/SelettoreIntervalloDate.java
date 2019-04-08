package banking.frontend;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.*;
import java.time.temporal.*;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class SelettoreIntervalloDate {

    private DatePicker selettoreDataInizio;
    private DatePicker selettoreDataFine;

    private AscoltatoreCambiaData dateChangeListener;

    private GridPane gridPane;

    public SelettoreIntervalloDate(LocalDate dataInizio, LocalDate dataFine) {

        // Se la data di inizio è maggiore alla data di fine imposto nei datepicker
        // la data odierna e la data odierna meno 30 giorni
        if (dataFine.isBefore(dataInizio)) {

            LocalDate today = LocalDate.now();

            selettoreDataInizio = new DatePicker(today.minus(30, ChronoUnit.DAYS));
            selettoreDataFine = new DatePicker(today);

        }else {

            selettoreDataInizio = new DatePicker(dataInizio);
            selettoreDataFine = new DatePicker(dataFine);

        }

        gridPane = new GridPane();
        gridPane.setHgap(16);
        gridPane.setVgap(8);
        gridPane.setPadding(new Insets(6, 4, 6, 4));


        // SEZIONE DI LOGIN
        Label etichettaInizioData = new Label("Periodo inizio :");
        gridPane.add(etichettaInizioData, 0, 0, 1, 1);
        gridPane.add(selettoreDataInizio, 0, 1, 1, 1);

        Label passwordLabel = new Label("Periodo fine :");
        gridPane.add(passwordLabel, 1, 0, 1, 1);
        gridPane.add(selettoreDataFine, 1, 1, 1, 1);

        selettoreDataInizio.setOnAction(e -> {
            if (selettoreDataInizio.getValue().isAfter(selettoreDataFine.getValue())) {
                selettoreDataFine.setValue(selettoreDataInizio.getValue().plus(1, ChronoUnit.DAYS));
            }

            LocalDate date = selettoreDataInizio.getValue();
            dateChangeListener.cambioIntervallo(date, selettoreDataFine.getValue());

        });

        selettoreDataFine.setOnAction(e -> {
            if (selettoreDataInizio.getValue().isAfter(selettoreDataFine.getValue())) {
                selettoreDataInizio.setValue(selettoreDataFine.getValue().minus(1, ChronoUnit.DAYS));
            }

            LocalDate date = selettoreDataFine.getValue();
            dateChangeListener.cambioIntervallo(selettoreDataInizio.getValue(), date);

        });

    }

    public LocalDate getDataInizio () {
        return this.selettoreDataInizio.getValue();
    }

    public LocalDate getDataFine() {
        return this.selettoreDataFine.getValue();
    }

    public GridPane getSelettoreDate() {
        return this.gridPane;
    }

    public void setAscoltatoreCambioData(AscoltatoreCambiaData dateChangeListener) {
        this.dateChangeListener = dateChangeListener;

    }

    public void richiediAggiornamentoDate() {
        this.dateChangeListener.cambioIntervallo(selettoreDataInizio.getValue(), selettoreDataFine.getValue());
    }

    public interface AscoltatoreCambiaData {

        void cambioDataInizio();
        void cambioDataFine();
        void cambioIntervallo(LocalDate dataInizio, LocalDate dataFine);
    }

}

