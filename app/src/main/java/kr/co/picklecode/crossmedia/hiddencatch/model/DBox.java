package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-19.
 */

public abstract class DBox {
    private int id;
    private String regDate;
    private String uptDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getUptDate() {
        return uptDate;
    }

    public void setUptDate(String uptDate) {
        this.uptDate = uptDate;
    }
}
