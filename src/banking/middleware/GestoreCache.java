package banking.middleware;

import java.io.*;
import java.time.*;

/**
 * Created by banking on 12/05/17.
 */
public class GestoreCache implements Serializable {

    private boolean valido;


    public LocalDate dataInizio;
    public LocalDate dataFine;

    private int cartaSelezionata;

    public GestoreCache(LocalDate dataInizio, LocalDate dataFine, int cartaSelezionata) {

        this.dataInizio = dataInizio;
        this.dataFine = dataFine;

        this.cartaSelezionata = cartaSelezionata;

    }

    public GestoreCache() {

        try (ObjectInputStream streamIngresso = new ObjectInputStream(new FileInputStream("./cache/cache.bin"))) {

            GestoreCache cache = (GestoreCache)streamIngresso.readObject();

            this.dataInizio = cache.dataInizio;
            this.dataFine = cache.dataFine;

            this.cartaSelezionata = cache.cartaSelezionata;

            this.valido = true;

        } catch (IOException | ClassNotFoundException e) {
            this.valido = false;

            e.printStackTrace();
        }

    }

    public void salvaCache() {

        try (ObjectOutputStream streamUscita = new ObjectOutputStream(new FileOutputStream("./cache/cache.bin"))) {

            streamUscita.writeObject(this);

        } catch (Exception e) {
            System.out.println("Errore durante il salvataggio del file: " + e.getLocalizedMessage());
        }

    }

    public GestoreCache ricaricaCache() {

        if (valido) {
            return this;
        }

        return null;
    }
}
