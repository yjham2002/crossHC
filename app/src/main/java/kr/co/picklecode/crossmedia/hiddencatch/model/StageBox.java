package kr.co.picklecode.crossmedia.hiddencatch.model;

import java.util.List;

/**
 * Created by HP on 2018-07-19.
 */

public class StageBox extends DBox{
    private int appId;
    private String stageDesc;
    private String originalPath;
    private int order;
    private List<QuestionBox> questions;

    private int count = 0;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getStageDesc() {
        return stageDesc;
    }

    public void setStageDesc(String stageDesc) {
        this.stageDesc = stageDesc;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<QuestionBox> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionBox> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "StageBox{" +
                "appId=" + appId +
                ", stageDesc='" + stageDesc + '\'' +
                ", originalPath='" + originalPath + '\'' +
                ", order=" + order +
                ", questions=" + questions +
                ", count=" + count +
                '}';
    }
}
