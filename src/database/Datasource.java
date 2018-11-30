package database;

public class Datasource {

    private Datasource instance;

    private Datasource(){}

    public Datasource getInstance() {
        if(instance == null) instance = new Datasource();
        return instance;
    }
}
