package kr.co.picklecode.crossmedia.hiddencatch.model;

import java.util.List;
import java.util.Vector;

/**
 * Created by HP on 2018-07-19.
 */

public class QuestionBox extends DBox {

    private int stageId;
    private String imgPath;
    private int order;
    private List<AnswerBox> answers;

    public int getStageId() {
        return stageId;
    }

    public void setStageId(int stageId) {
        this.stageId = stageId;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public List<AnswerBox> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerBox> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionBox{" +
                "stageId=" + stageId +
                ", imgPath='" + imgPath + '\'' +
                ", order=" + order +
                ", answers=" + answers +
                '}';
    }
}
