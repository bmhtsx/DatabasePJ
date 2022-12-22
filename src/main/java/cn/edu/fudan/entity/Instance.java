package cn.edu.fudan.entity;

import java.sql.Timestamp;

public class Instance {
    private int id;
    private int commitId;
    private String severity;
    private String type;
    private String status;
    private String author;
    private String message;
//    private Timestamp creationDate;
//    private Timestamp updateDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommitId() {
        return commitId;
    }

    public void setCommitId(int commitId) {
        this.commitId = commitId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", commitId=" + commitId +
                ", severity='" + severity + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", author='" + author + '\'' +
                ", message='" + message + '\'';
    }
//    public Timestamp getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Timestamp creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    public Timestamp getUpdateDate() {
//        return updateDate;
//    }
//
//    public void setUpdateDate(Timestamp updateDate) {
//        this.updateDate = updateDate;
//    }
}
