package com.vyas.pranav.mycookbook.modelsutils;

import java.util.List;

public class MainRecepieModel {
    private int id,servings;
    private String name,image;
    private List<MainIngrediantsModel> ingredients;
    private List<MainStepsModel> steps;

    public MainRecepieModel(int id, int servings, String name, String image, List<MainIngrediantsModel> ingredients, List<MainStepsModel> steps) {
        this.id = id;
        this.servings = servings;
        this.name = name;
        this.image = image;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    public MainRecepieModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<MainIngrediantsModel> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<MainIngrediantsModel> ingredients) {
        this.ingredients = ingredients;
    }

    public List<MainStepsModel> getSteps() {
        return steps;
    }

    public void setSteps(List<MainStepsModel> steps) {
        this.steps = steps;
    }
}
