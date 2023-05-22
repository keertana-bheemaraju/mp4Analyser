package com.castlabs.mp4Analyzer.model;

import java.util.List;

public class Box {

    private String type;

    private Integer size;

    private List<Box> subBoxes;

    public Box(String type, Integer size, List<Box> subBoxes) {
        this.type = type;
        this.size = size;
        this.subBoxes = subBoxes;
    }

    public Box(String type, Integer size) {
        this(type, size, null);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Box> getSubBoxes() {
        return subBoxes;
    }

    public void setSubBoxes(List<Box> subBoxes) {
        this.subBoxes = subBoxes;
    }

}
