package com.tomdoesburg.model;

/**
 * Created by Tom on 27/6/14.
 */
public class Vegetable {
    private int id;
    private String name;
    private int cookingTime; //cooking time in seconds
    private String description;

    public Vegetable(){}

    public Vegetable(String name, int cookingTime, String description) {
        this.name = name;
        this.cookingTime = cookingTime;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Vegetable{" +
                "name='" + name + '\'' +
                ", cookingTime=" + cookingTime +
                '}';
    }
}
