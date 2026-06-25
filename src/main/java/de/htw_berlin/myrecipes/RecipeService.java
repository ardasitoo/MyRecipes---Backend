package de.htw_berlin.myrecipes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecipeService {

    private static final String DEFAULT_OWNER = "Simar";

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> getAllRecipes(String ownerName) {
        if (hasText(ownerName)) {
            return recipeRepository.findByOwnerNameOrderByIdAsc(ownerName.trim());
        }

        List<Recipe> recipes = new ArrayList<>();
        recipeRepository.findAll().forEach(recipes::add);
        return recipes;
    }

    public Recipe getRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden"));
    }

    public Recipe createRecipe(Recipe recipe) {
        normalizeOwner(recipe);
        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        Recipe recipe = getRecipe(id);

        recipe.setName(updatedRecipe.getName());
        recipe.setDescription(updatedRecipe.getDescription());
        recipe.setCategory(updatedRecipe.getCategory());
        recipe.setPreparationTime(updatedRecipe.getPreparationTime());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setSteps(updatedRecipe.getSteps());
        recipe.setOwnerName(hasText(updatedRecipe.getOwnerName()) ? updatedRecipe.getOwnerName().trim() : recipe.getOwnerName());

        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden");
        }

        recipeRepository.deleteById(id);
    }

    private void normalizeOwner(Recipe recipe) {
        if (!hasText(recipe.getOwnerName())) {
            recipe.setOwnerName(DEFAULT_OWNER);
            return;
        }

        recipe.setOwnerName(recipe.getOwnerName().trim());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
