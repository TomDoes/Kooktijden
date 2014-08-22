package com.tomdoesburg.kooktijden.vegetables;

/**
 * Created by Tom on 17/8/14.
 */
public class VegetableListItem {
    private int id;
    private String vegetableName;

    public String getVegetableName() {
        return vegetableName;
    }

    public void setVegetableName(String vegetableName) {
        this.vegetableName = vegetableName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VegetableListItem(String name, int id) {
        this.vegetableName = name;
        this.id = id;
    }
}
