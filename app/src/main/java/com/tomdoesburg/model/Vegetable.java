package com.tomdoesburg.model;

/**
 * Created by Tom on 27/6/14.
 */
public class Vegetable {
    private int id;
    private String nameEN;
    private String nameNL;
    private int cookingTimeMin;
    private int cookingTimeMax;
    private String descriptionEN;
    private String descriptionNL;

    public Vegetable() {

    }

    public Vegetable(String nameEN, String nameNL, int cookingTimeMin, int cookingTimeMax, String descriptionEN, String descriptionNL) {
        this.nameEN = nameEN;
        this.nameNL = nameNL;
        this.cookingTimeMin = cookingTimeMin;
        this.cookingTimeMax = cookingTimeMax;
        this.descriptionEN = descriptionEN;
        this.descriptionNL = descriptionNL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getNameNL() {
        return nameNL;
    }

    public void setNameNL(String nameNL) {
        this.nameNL = nameNL;
    }

    public int getCookingTimeMin() {
        return cookingTimeMin;
    }

    public void setCookingTimeMin(int cookingTimeMin) {
        this.cookingTimeMin = cookingTimeMin;
    }

    public int getCookingTimeMax() {
        return cookingTimeMax;
    }

    public void setCookingTimeMax(int cookingTimeMax) {
        this.cookingTimeMax = cookingTimeMax;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getDescriptionNL() {
        return descriptionNL;
    }

    public void setDescriptionNL(String descriptionNL) {
        this.descriptionNL = descriptionNL;
    }

    @Override
    public String toString() {
        return "Vegetable{" +
                "id=" + id +
                ", nameEN='" + nameEN + '\'' +
                ", nameNL='" + nameNL + '\'' +
                ", cookingTimeMin=" + cookingTimeMin +
                ", cookingTimeMax=" + cookingTimeMax +
                ", descriptionEN='" + descriptionEN + '\'' +
                ", descriptionNL='" + descriptionNL + '\'' +
                '}';
    }
}
