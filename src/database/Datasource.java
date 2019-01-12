package database;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * The singleton class that holds all of the data from the database.
 * @author Zack Dreitzler
 * @version .05
 */
public class Datasource {
    private static String databaseURL = "jdbc:postgresql://localhost:5432/payrollmanagement";
    private static String user = "postgres";
    private static String pass = "KillerMan&17$";
    private static Connection connection;
    private int currentEmployeeSSN = 0;
    private static final String SHIFT_TABLE_NAME = "shifts";
    private static final String LOGIN_TABLE_NAME = "login_cred";
    private static final String LOGIN_USERNAME_COLUMN = "uname";
    private static final String LOGIN_PASSWORD_COLUMN = "pass";
    private static final String QUERY_LOGIN = "SELECT * FROM " + LOGIN_TABLE_NAME + " WHERE " + LOGIN_USERNAME_COLUMN +
            " = ? AND " + LOGIN_PASSWORD_COLUMN + " = ?;";

    private static final String SHIFT_ESSN_COLUMN = "essn";
    private static final String SHIFT_INTIME_COLUMN = "in_time";
    private static final String SHIFT_OUTTIME_COLUMN = "out_time";
    private static final String QUERY_CLOCKED_IN = "SELECT * FROM " + SHIFT_TABLE_NAME +
    " WHERE " + SHIFT_ESSN_COLUMN + " = ? AND " + SHIFT_OUTTIME_COLUMN + " IS NULL;";
    private static final String CLOCK_IN_QUERY = "INSERT INTO " + SHIFT_TABLE_NAME + "(" + SHIFT_ESSN_COLUMN +
        "," + SHIFT_INTIME_COLUMN + ") VALUES (?,?);";
    private static final String CLOCK_OUT_QUERY = "UPDATE " + SHIFT_TABLE_NAME + " SET " + SHIFT_OUTTIME_COLUMN +
            " = ? WHERE " + SHIFT_ESSN_COLUMN + " = ? AND " + SHIFT_OUTTIME_COLUMN + " IS NULL;";
    private static final String QUERY_ALL_SHIFTS = "SELECT * FROM " + SHIFT_TABLE_NAME + " WHERE " + SHIFT_ESSN_COLUMN +
            " = ?;";


    private static final String EMPLOYEE_TABLE_NAME = "employee";
    private static final String EMPLOYEE_SSN_COLUMN = "ssn";
    private static final String EMPLOYEE_MSSN_COLUMN = "mssn";
    private static final String EMPLOYEE_ADDRESS_COLUMN = "address";
    private static final String EMPLOYEE_PAYRATE_COLUMN = "payrate";
    private static final String EMPLOYEE_DNUM_COLUMN = "dnum";
    private static final String EMPLOYEE_FIRSTNAME_COLUMN = "fname";
    private static final String EMPLOYEE_LASTNAME_COLUMN = "lname";
    private static final String QUERY_EMPLOYEE_INFO = "SELECT * FROM " + EMPLOYEE_TABLE_NAME + " WHERE " + EMPLOYEE_SSN_COLUMN
            + " = ?;";

    private static final String UPDATE_EMPLOYEE_INFO = "UPDATE " + EMPLOYEE_TABLE_NAME + " SET " +EMPLOYEE_MSSN_COLUMN +
            " = ?, " + EMPLOYEE_PAYRATE_COLUMN + " = ? WHERE " + EMPLOYEE_SSN_COLUMN + " = ?;";

    private static final String DELETE_EMPLOYEE_EMPLOYEETABLE = "DELETE FROM " + EMPLOYEE_TABLE_NAME + " WHERE " +
            EMPLOYEE_SSN_COLUMN + " = ?;";
    private static final String DELETE_EMPLOYEE_LOGINTABLE = "DELETE FROM " + LOGIN_TABLE_NAME + " WHERE " +
            EMPLOYEE_SSN_COLUMN + " = ?;";
    private static final String DELETE_EMPLOYEE_SHIFTTABLE = "DELETE FROM " + SHIFT_TABLE_NAME + " WHERE " +
            EMPLOYEE_SSN_COLUMN + " = ?;";


    private static PreparedStatement deleteEmployeeFromEmployee;
    private static PreparedStatement deleteEmployeeFromShift;
    private static PreparedStatement deleteEmployeeFromLogin;
    private static PreparedStatement updateEmpInfo;
    private static PreparedStatement queryLogin;
    private static PreparedStatement queryClockStatus;
    private static PreparedStatement queryEmployeeInfo;
    private static PreparedStatement clockInEmployee;
    private static PreparedStatement clockOutEmployee;
    private static PreparedStatement getEmployeeShfits;
    private static Datasource instance;

    private Datasource(){}

    /**
     * Gets the singleton instance of datasource.
     * @return instance of datasource
     */
    public static Datasource getInstance() {
        if(instance == null) instance = new Datasource();
        return instance;
    }

    /**
     * Gets the SSN of the currently logged in Employee.
     * @return int SSN of the current employee.
     */
    public int getCurrentEmployeeSSN(){
        return currentEmployeeSSN;
    }

    /**
     * Initializes the connection to the database and all prepared
     * SQL statements.
     * @return true if connection is successful.
     */
    public boolean open(){
        try{
            connection  =  DriverManager.getConnection(databaseURL, user, pass);
            queryLogin = connection.prepareStatement(QUERY_LOGIN);
            queryClockStatus = connection.prepareStatement(QUERY_CLOCKED_IN);
            queryEmployeeInfo = connection.prepareStatement(QUERY_EMPLOYEE_INFO);
            updateEmpInfo = connection.prepareStatement(UPDATE_EMPLOYEE_INFO);
            deleteEmployeeFromEmployee = connection.prepareStatement(DELETE_EMPLOYEE_EMPLOYEETABLE);
            deleteEmployeeFromLogin = connection.prepareStatement(DELETE_EMPLOYEE_LOGINTABLE);
            deleteEmployeeFromShift = connection.prepareStatement(DELETE_EMPLOYEE_SHIFTTABLE);
            clockInEmployee = connection.prepareStatement(CLOCK_IN_QUERY);
            clockOutEmployee = connection.prepareStatement(CLOCK_OUT_QUERY);
            getEmployeeShfits = connection.prepareStatement(QUERY_ALL_SHIFTS);
            return true;

        }catch (SQLException e){
            System.out.println("Failed to connect to database");
           // e.printStackTrace();
            return false;
        }
    }

    /**
     * Closes the connection to the database and all prepared statements.
     * @return true if close is successful
     */
    public boolean close(){
        try{
            if(queryLogin != null){
                queryLogin.close();
            }
            if (queryClockStatus != null){
                queryClockStatus.close();
            }
            if(queryEmployeeInfo != null){
                queryEmployeeInfo.close();
            }
            if(updateEmpInfo != null){
                updateEmpInfo.close();
            }
            if(deleteEmployeeFromShift != null){
                deleteEmployeeFromShift.close();
            }
            if(deleteEmployeeFromLogin != null){
                deleteEmployeeFromLogin.close();
            }
            if(deleteEmployeeFromEmployee != null){
                deleteEmployeeFromEmployee.close();
            }
            if(clockInEmployee != null){
                clockInEmployee.close();
            }
            if(clockOutEmployee != null){
                clockOutEmployee.close();
            }
            if(getEmployeeShfits != null){
                getEmployeeShfits.close();
            }
            return true;
        }catch(SQLException e){
            System.out.println("Couldn't close connection: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks the database for the given username and password.
     * @param uname
     * @param pass
     * @return true if login is successful.
     */
    public boolean queryUserLogin(String uname, String pass){
        try{
            queryLogin.setString(1, uname);
            queryLogin.setString(2, pass);
            ResultSet results = queryLogin.executeQuery();
            results.next();
            if (results.getString(2).equals(uname) && results.getString(3).equals(pass)){
                currentEmployeeSSN = results.getInt(1);
                return true;
            }
            return false;
        }catch(SQLException e){
            System.out.println("Login Error " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks to see if the given employee is clocked in or out.
     * @return true if employee is clocked in.
     */
    public boolean queryEmployeeClockStatus(){
        try{
            queryClockStatus.setInt(1, currentEmployeeSSN);
            ResultSet results = queryClockStatus.executeQuery();
            if(results.next()){
                if (results.getTimestamp(3) == null){
                    return true;
                }
            }
            return false;
        }catch(SQLException e){
            System.out.println("Clock Error " + e.getMessage());
            return false;
        }
    }

    /**
     * Queries all of the given Employee's information.
     * @param ssn is the employees id number
     * @return The employee's information
     */
    public ResultSet getEmployeeInfo(int ssn){
        try{
            queryEmployeeInfo.setInt(1, ssn);
            ResultSet results = queryEmployeeInfo.executeQuery();
            return results;
        }catch(SQLException e){
            System.out.println("Clock Error " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates an employee in the database.
     * @param mssn the manager's ssn number.
     * @param payrate the payrate of this employee.
     * @param ssn the employee's id number.
     * @return true if it successfully updates the employee
     */
    public boolean updateEmployeeInfo(int ssn, int mssn, float payrate){
        try{
            updateEmpInfo.setInt(1, mssn);
            updateEmpInfo.setFloat(2, payrate);
            updateEmpInfo.setInt(3, ssn);
            updateEmpInfo.executeQuery();
            return true;
        }catch(SQLException e){
            System.out.println("Error updating" + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes employee from the database.
     * @param ssn the employees id number.
     * @return true if the deletion is successful.
     */
    public boolean deleteEmployee(int ssn){
        try{
            deleteEmployeeFromEmployee.setInt(1,ssn);
            deleteEmployeeFromLogin.setInt(1,ssn);
            deleteEmployeeFromShift.setInt(1,ssn);
            deleteEmployeeFromEmployee.executeQuery();
            deleteEmployeeFromLogin.executeQuery();
            deleteEmployeeFromShift.executeQuery();
            return true;
        }catch(SQLException e){
            System.out.println("Error deleting" + e.getMessage());
            return false;
        }
    }

    /**
     * Clocks the employee in.
     * @return true if employee is clocked in successfully.
     */
    public boolean clockInEmployeeOnTable(){
        try{
            Timestamp time = Timestamp.valueOf(LocalDateTime.now());
            clockInEmployee.setInt(1, currentEmployeeSSN);
            clockInEmployee.setTimestamp(2, time);
            clockInEmployee.executeQuery();
            return true;
        }catch (SQLException e){
            System.out.println("Error Clocking Employee in. " + e.getMessage());
            return false;
        }
    }

    /**
     * Clocks employee out.
     * @return true if employee is clocked out successfully.
     */
    public boolean clockOutEmployeeOnTable(){
        try{
            Timestamp time = Timestamp.valueOf(LocalDateTime.now());
            clockOutEmployee.setTimestamp(1, time);
            clockOutEmployee.setInt(2, currentEmployeeSSN);
            clockOutEmployee.executeQuery();
            return true;
        }catch (SQLException e) {
            System.out.println("Error Clocking Employee out. " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets all of the shifts of the current employee.
     * @return ResultSet of the executed SQL query.
     */
    public ResultSet getCurrentEmployeeShifts(){
        try{
            getEmployeeShfits.setInt(1, currentEmployeeSSN);
            ResultSet resultSet = getEmployeeShfits.executeQuery();
            return resultSet;
        }catch (SQLException e){
            System.out.println("Error getting employee's shifts. " + e.getMessage());
            return null;
        }
    }
}
