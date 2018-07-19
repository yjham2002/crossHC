package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-19.
 */

public class AnswerBox extends DBox {
    private int questionId;
    private double coordX;
    private double coordY;
    private double threshold;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
