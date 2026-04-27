package Global;

import java.io.*;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    static Configuration instance = null;
    Properties prop;
    Logger logger;
    String typeSequences;

    public static Configuration instance() {
        if (instance == null)
            instance = new Configuration();
        return instance;
    }

    static void chargerProprietes(Properties p, InputStream in, String nom) {

        try {
            p.load(in);
        } catch (IOException e) {
            // Le logger n'est pas encore en place à ce moment là
            System.err.println("Impossible de charger " + nom);
            System.err.println(e.toString());
            System.exit(1);
        }
    }

    protected Configuration() {
        // On charge les propriétés

        InputStream in = ouvre("defaut.cfg");
        Properties defaut = new Properties();
        chargerProprietes(defaut, in, "defaut.cfg");
        // Il faut attendre le dernier moment pour utiliser le logger
        // car celui-ci s'initialise avec les propriétés
        String message = "Fichier de propriétés defaut.cfg chargé";
        String nom = System.getProperty("user.home") + File.separator + ".waffle";
        try {
            in = new FileInputStream(nom);
            prop = new Properties(defaut);
            chargerProprietes(prop, in, nom);
            logger().info(message);
            logger().info("Fichier de propriétés " + nom + " chargé");
        } catch (FileNotFoundException e) {
            prop = defaut;
            logger().info(message);
        }
        //typeSequences = lis("Sequences");
    }

    public String lis(String nom) {
        String value = prop.getProperty(nom);
        if (value != null) {
            return value;
        } else {
            throw new NoSuchElementException("Propriété " + nom + " manquante");
        }
    }

    public static String lisChaine(String nom) {
        return instance().lis(nom);
    }

    public static int lisInt(String nom) {
        return Integer.parseInt(instance().lis(nom));
    }

    public static double lisDouble(String nom) {
        return Double.parseDouble(instance().lis(nom));
    }

    public static boolean lisBoolean(String nom) {
        return Boolean.parseBoolean(instance().lis(nom));
    }

    public Logger logger() {
        if (logger == null) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s : %5$s%n");
            logger = Logger.getLogger("Waffle.Logger");
            logger.setLevel(Level.parse(lis("LogLevel")));
        }
        return logger;
    }

    public static InputStream ouvre(String s) {
        // La méthode de chargement suivante ne dépend pas du système de fichier
        // et sera donc utilisable pour un .jar
        // Attention, par contre, le fichier doit se trouver dans le CLASSPATH

        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(s);
        if (in == null) {
            try{
                in = new FileInputStream("res/" + s);
            } catch (FileNotFoundException e) {
                System.err.println("impossible de charger le ressource " + s);
                System.exit(1);
            }
        }
        return in;
    }

    public static void info(String s) {
        instance().logger().info(s);
    }

    public static void alerte(String s) {
        instance().logger().warning(s);
    }

    public static void erreur(String s) {
        instance().logger().severe(s);
        System.exit(1);
    }
}


