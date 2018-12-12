package database;

import java.sql.*;

/**
 * The singleton class that holds all of the data from the database.
 * @author Zack Dreitzler
 * @version .01
 */
public class Datasource {
    private static String databaseURL = "jdbc:postgresql://localhost:5432/example";
    private static String user = "postgres";
    private static String pass = "MKillerMan&17$";
    private static Connection connection;

    private static String LOGIN_TABLE_NAME = "login_cred";
    private static String LOGIN_USERNAME_COLUMN = "uname";
    private static String LOGIN_PASSWORD_COLUMN = "pass";
    private static String QUERY_LOGIN = "SELECT * FROM" + LOGIN_TABLE_NAME + " WHERE " + LOGIN_USERNAME_COLUMN +
            " = ? AND " + LOGIN_PASSWORD_COLUMN + " = ?";

    private static PreparedStatement queryLogin;
    private static Datasource instance;

    private Datasource(){}

    public static Datasource getInstance() {
        if(instance == null) instance = new Datasource();
        return instance;
    }

    public boolean open(){
        try{
            connection  =  DriverManager.getConnection(databaseURL, user, pass);
            queryLogin = connection.prepareStatement(QUERY_LOGIN);
            return true;

        }catch (SQLException e){
            System.out.println("Failed to connect to database");
            return false;
        }
    }

    public boolean close(){
        try{
            if(queryLogin != null){
                queryLogin.close();
            }
            return true;
        }catch(SQLException e){
            System.out.println("Couldn't close connection: " + e.getMessage());
            return false;
        }
    }

    public boolean queryUserLogin(String uname, String pass){
        try{
            queryLogin.setString(1, uname);
            queryLogin.setString(2, pass);
            ResultSet results = queryLogin.executeQuery();
            if (results.getString(0).equals(uname) && results.getString(2).equals(pass)){
                return true;
            }
            return false;
        }catch(SQLException e){
            System.out.println("Login Error " + e.getMessage());
            return false;
        }
    }

}
