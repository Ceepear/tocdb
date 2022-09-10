package net.t1y.cloud3.sdk.database.form;

public abstract class Form {
    private String table;
    private int timestamp;
    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
