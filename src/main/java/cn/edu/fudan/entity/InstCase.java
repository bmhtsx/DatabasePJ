package cn.edu.fudan.entity;

import java.sql.Time;
import java.sql.Timestamp;

public class InstCase {
    private int id;
    private String status;
    private String type;
    private int instLast;
    private int commitNew;
    private int commitLast;
    private Timestamp createTime;
    private Timestamp updateTime;
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

    public int getInstLast() {
        return instLast;
    }

    public void setInstLast(int instLast) {
        this.instLast = instLast;
    }

    public int getCommitNew() {
        return commitNew;
    }

    public void setCommitNew(int commitNew) {
        this.commitNew = commitNew;
    }

    public int getCommitLast() {
        return commitLast;
    }

    public void setCommitLast(int commitLast) {
        this.commitLast = commitLast;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "InstCase{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", instLast=" + instLast +
                ", commitNew=" + commitNew +
                ", commitLast=" + commitLast +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", committerNew='" + committerNew + '\'' +
                ", committerLast='" + committerLast + '\'' +
                ", durationTime=" + durationTime +
                '}';
    }
}
