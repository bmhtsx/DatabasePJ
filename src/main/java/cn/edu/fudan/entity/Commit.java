package cn.edu.fudan.entity;

public class Commit {
    private int id;
    private String commitHash;
    private String branch;
    private String repository;
    private String committer;
    private String commitTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(String commitTime) {
        this.commitTime = commitTime;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id=" + id +
                ", commitHash='" + commitHash + '\'' +
                ", branch='" + branch + '\'' +
                ", repository='" + repository + '\'' +
                ", committer='" + committer + '\'' +
                ", commitTime='" + commitTime + '\'' +
                '}';
    }
}
