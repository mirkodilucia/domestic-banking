package banking.middleware;

/**
 * Created by banking on 12/05/17.
 */
public class GestoreConfigurazione {

    private static Configurazione config;

    public static Configurazione getConfiguratione() {

        if (config == null) {
            config = caricaConfiguratione("./res/xml/config.xml", "./res/xml/config.xsd");
        }

        return config;
    }

    private static Configurazione caricaConfiguratione(String xmlPath, String xsdPath) {

        GestoreXML xmlManager = new GestoreXML(xmlPath, xsdPath);
        xmlManager.getStreamXML().alias("Configurazione", Configurazione.class);
        return (Configurazione) xmlManager.caricaOggettoXML();

    }

}
