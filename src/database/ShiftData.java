package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ShiftData {
    private ObservableList<Shift> shifts;

    public ShiftData(){
        shifts = FXCollections.observableArrayList();
    }

    public ObservableList<Shift> getShifts() {
        return shifts;
    }

    /**
     * Takes all current shifts of current employee and adds the to
     * the Observable list.
     */
    public void readInShifts(Datasource datasource){
        ResultSet resultSet = datasource.getCurrentEmployeeShifts();
        try{
            while (resultSet.next()){
                int ssn = resultSet.getInt(1);
                Timestamp intime = resultSet.getTimestamp(2);
                Timestamp outtime = resultSet.getTimestamp(3);
                Shift tempShift = new Shift();
                tempShift.setSsn(ssn);
                tempShift.setInTime(intime.toLocalDateTime());
                if(outtime != null){
                    tempShift.setOutTime(outtime.toLocalDateTime());
                }
                shifts.add(tempShift);
            }
        }catch (SQLException e){
            System.out.println("Error checking shift query. " + e.getMessage());
        }
    }
}
