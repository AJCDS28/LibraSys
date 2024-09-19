package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei Jorge
 */
public class Conexao {
    private Connection con;
    
    public Conexao() {
        String driver = "org.postgresql.Driver";
        String user = "postgres";
        String senha = "udesc";
        String url = "jdbc:postgresql://localhost:5432/libraSys";

        try {
            Class.forName(driver);
            this.con = (Connection) DriverManager.getConnection(url, user, senha);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }  
    }
    
    public Connection getConnection() {
        return con;
    }
    
    public void closeConnection(){
        try {
            this.con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}