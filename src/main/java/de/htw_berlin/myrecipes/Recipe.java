package de.htw_berlin.myrecipes;

public class Recipe {

    String name;

    public Recipe() {}

    public Recipe(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
