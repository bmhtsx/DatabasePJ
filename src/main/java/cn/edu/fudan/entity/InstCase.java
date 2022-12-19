package cn.edu.fudan.entity;

public class InstCase {
    private int id;
    private String status;
    private String type;
    private String commitNew;
    private String commitLast;
    private String createTime;
    private String updateTime;
    private String committerNew;
    private String committerLast;
    private int durationTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCommitNew() {
        return commitNew;
    }

    public void setCommitNew(String commitNew) {
        this.commitNew = commitNew;
    }

    public String getCommitLast() {
        return commitLast;
    }

    public void setCommitLast(String commitLast) {
        this.commitLast = commitLast;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCommitterNew() {
        return committerNew;
    }

    public void setCommitterNew(String committerNew) {
        this.committerNew = committerNew;
    }

    public String getCommitterLast() {
        return committerLast;
    }

    public void setCommitterLast(String committerLast) {
        this.committerLast = committerLast;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    @Override
    public String toString() {
        return "InstCase{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", commitNew='" + commitNew + '\'' +
                ", commitLast='" + commitLast + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", committerNew='" + committerNew + '\'' +
                ", committerLast='" + committerLast + '\'' +
                ", durationTime=" + durationTime +
                '}';
    }
}
