package net.t1y.cloud3.sdk.database.form;

public class Update extends Form {
    private String set;
    private String where;

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
