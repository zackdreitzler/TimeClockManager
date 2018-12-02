package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Datasource {
    private static String databaseURL = "jdbc:postgresql://localhost:5432/example";
    private static String user = "postgres";
    private static String pass = "MyPass";

    private Datasource instance;

    private Datasource(){}

    public Datasource getInstance() {
        if(instance == null) instance = new Datasource();
        return instance;
    }

    public Connection getConnection(){
        try (Connection connection = DriverManager
                .getConnection(databaseURL, user, pass)){
            return connection;
        }catch (SQLException e){
            System.out.println("Failed to connect to database");
            return null;
        }
    }
}
