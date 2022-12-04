package cn.edu.fudan.entity;

public class Location {
    private String component;
    private int startLine;
    private int endLine;
    private int startOffset;
    private int endOffset;
    private int instId;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getInstId() {
        return instId;
    }

    public void setInstId(int instId) {
        this.instId = instId;
    }
}
