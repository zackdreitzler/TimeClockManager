package database;

import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDateTime;

/**
 * The Shift class is one shift pulled from the database.
 * @author Zack Dreitzler
 * @version .01
 */
public class Shift {
    SimpleIntegerProperty ssn;
    LocalDateTime inTime;
    LocalDateTime outTime;

    public Shift() {
        this.ssn = new SimpleIntegerProperty();
    }

    public int getSsn() {
        return ssn.get();
    }

    public SimpleIntegerProperty ssnProperty() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn.set(ssn);
    }

    public LocalDateTime getInTime() {
        return inTime;
    }

    public void setInTime(LocalDateTime inTime) {
        this.inTime = inTime;
    }

    public LocalDateTime getOutTime() {
        return outTime;
    }

    public void setOutTime(LocalDateTime outTime) {
        this.outTime = outTime;
    }
}
