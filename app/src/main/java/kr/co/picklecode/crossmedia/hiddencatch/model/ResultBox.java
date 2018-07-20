package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-20.
 */

public class ResultBox {

    private StageBox stageBox;
    private int continuous = -1;
    private int currentPosition = -1;
    private boolean isHintUsed = false;
    private boolean isHeartUsed = false;

    public ResultBox(StageBox stageBox, int progressInChallenge, int currentPosition, boolean isHintUsed, boolean isHeartUsed) {
        this.stageBox = stageBox;
        this.continuous = progressInChallenge;
        this.currentPosition = currentPosition;
        this.isHintUsed = isHintUsed;
        this.isHeartUsed = isHeartUsed;
    }

    @Override
    public String toString() {
        return "ResultBox{" +
                "stageBox=" + stageBox +
                ", continuous=" + continuous +
                ", currentPosition=" + currentPosition +
                ", isHintUsed=" + isHintUsed +
                ", isHeartUsed=" + isHeartUsed +
                '}';
    }

    public StageBox getStageBox() {
        return stageBox;
    }

    public void setStageBox(StageBox stageBox) {
        this.stageBox = stageBox;
    }

    public int getContinuous() {
        return continuous;
    }

    public void setContinuous(int continuous) {
        this.continuous = continuous;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public boolean isHintUsed() {
        return isHintUsed;
    }

    public void setHintUsed(boolean hintUsed) {
        isHintUsed = hintUsed;
    }

    public boolean isHeartUsed() {
        return isHeartUsed;
    }

    public void setHeartUsed(boolean heartUsed) {
        isHeartUsed = heartUsed;
    }
}
