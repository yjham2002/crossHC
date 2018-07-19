package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-19.
 */

public class AppBox extends DBox {
    private String appName;
    private String appDesc;
    private int version;

    @Override
    public String toString() {
        return "AppBox{" +
                "appName='" + appName + '\'' +
                ", appDesc='" + appDesc + '\'' +
                ", version=" + version +
                '}';
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
