package gestionfrais;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Variable de connexion au niveau de l'application
    private static Connection con = null;

    public static Connection getConnection() throws SQLException {
        // Paramètres de connexion (À adapter si votre mot de passe n'est pas "")
        String URL = "jdbc:mysql://localhost/frais_scolaire";
        String USER = "root";
        String PASS = ""; 

        if (con == null || con.isClosed()) {
            // S'assurer que le driver est chargé
            // Class.forName("com.mysql.cj.jdbc.Driver"); // N'est plus nécessaire dans les versions récentes
            con = DriverManager.getConnection(URL, USER, PASS);
        }
        return con;
    }
}