package com.castlabs.mp4Analyzer.model;

public class StackEntry {

    private Box box;

    private Integer endByte;

    public StackEntry(Box box, Integer endByte) {
        this.box = box;
        this.endByte = endByte;
    }

    public Box getBox() {
        return box;
    }

    public void setBox(Box box) {
        this.box = box;
    }

    public Integer getEndByte() {
        return endByte;
    }

    public void setEndByte(Integer endByte) {
        this.endByte = endByte;
    }
}
