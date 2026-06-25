package de.htw_berlin.myrecipes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;
    private Integer preparationTime;
    private String ingredients;
    private String steps;
    private String ownerName;
    private Boolean favorite;

    public Recipe() {
    }

    public Recipe(String name) {
        this.name = name;
    }

    public Recipe(String name, String description, String category, Integer preparationTime, String ingredients, String steps) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.preparationTime = preparationTime;
        this.ingredients = ingredients;
        this.steps = steps;
        this.ownerName = "Simar";
        this.favorite = false;
    }

    public Recipe(String name, String description, String category, Integer preparationTime, String ingredients, String steps, String ownerName) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.preparationTime = preparationTime;
        this.ingredients = ingredients;
        this.steps = steps;
        this.ownerName = ownerName;
        this.favorite = false;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}
