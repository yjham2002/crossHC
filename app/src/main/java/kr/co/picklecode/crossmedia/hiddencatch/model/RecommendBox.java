package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-19.
 */

public class RecommendBox extends DBox {
    private int appId;
    private String appName;
    private String appDesc;
    private int order;
    private String packageName;
    private int exposure;
    private String imgPath;

    @Override
    public String toString() {
        return "RecommendBox{" +
                "appId=" + appId +
                ", appName='" + appName + '\'' +
                ", appDesc='" + appDesc + '\'' +
                ", order=" + order +
                ", packageName='" + packageName + '\'' +
                ", exposure=" + exposure +
                ", imgPath='" + imgPath + '\'' +
                '}';
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getExposure() {
        return exposure;
    }

    public void setExposure(int exposure) {
        this.exposure = exposure;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
