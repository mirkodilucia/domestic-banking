package banking.middleware.exception;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class SaldoNonSufficienteException extends Exception {

    public SaldoNonSufficienteException() {
        super();
    }

    public SaldoNonSufficienteException(String message) {
        super(message);
    }
}
