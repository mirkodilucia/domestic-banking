package banking.middleware.exception;

/**
 * Created by mirkodilucia on 08/06/17.
 */
public class ClienteNonTrovatoException extends Exception {

    public ClienteNonTrovatoException() {

    }

    public ClienteNonTrovatoException(String message) {
        super(message);
    }

}
