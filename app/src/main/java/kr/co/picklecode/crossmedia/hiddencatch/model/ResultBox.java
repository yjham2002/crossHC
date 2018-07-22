package kr.co.picklecode.crossmedia.hiddencatch.model;

/**
 * Created by HP on 2018-07-20.
 */

public class ResultBox {

    private StageBox stageBox;
    private int currentPosition = 0;
    private boolean isHintUsed = false;
    private boolean isHeartUsed = false;
    private boolean isLosed = false;
    private boolean isChallenge = false;

    private boolean isReplay = false;

    public ResultBox(){}

    public void initForNewGame(){
        this.stageBox = null;
        this.isHintUsed = false;
        this.isHeartUsed = false;
        this.isLosed = false;
        this.isReplay = false;
    }

    public boolean isReplay() {
        return isReplay;
    }

    public void setReplay(boolean replay) {
        isReplay = replay;
    }

    @Override
    public String toString() {
        return "ResultBox{" +
                "stageBox=" + stageBox +
                ", currentPosition=" + currentPosition +
                ", isHintUsed=" + isHintUsed +
                ", isHeartUsed=" + isHeartUsed +
                ", isLosed=" + isLosed +
                ", isChallenge=" + isChallenge +
                '}';
    }

    public boolean isLosed() {
        return isLosed;
    }

    public void setLosed(boolean losed) {
        isLosed = losed;
    }

    public boolean isChallenge() {
        return isChallenge;
    }

    public void setChallenge(boolean challenge) {
        isChallenge = challenge;
    }

    public StageBox getStageBox() {
        return stageBox;
    }

    public void setStageBox(StageBox stageBox) {
        this.stageBox = stageBox;
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
