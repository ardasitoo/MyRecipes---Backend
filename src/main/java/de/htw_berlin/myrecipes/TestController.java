package de.htw_berlin.myrecipes;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class TestController {

    private final RecipeRepository recipeRepository;

    public TestController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @GetMapping("/recipes")
    public Iterable<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @GetMapping("/recipes/{id}")
    public Recipe getRecipe(@PathVariable Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden"));
    }

    @PostMapping("/recipes")
    public Recipe createRecipe(@RequestBody Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @PutMapping("/recipes/{id}")
    public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe updatedRecipe) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden"));

        recipe.setName(updatedRecipe.getName());
        recipe.setDescription(updatedRecipe.getDescription());
        recipe.setCategory(updatedRecipe.getCategory());
        recipe.setPreparationTime(updatedRecipe.getPreparationTime());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setSteps(updatedRecipe.getSteps());

        return recipeRepository.save(recipe);
    }

    @DeleteMapping("/recipes/{id}")
    public void deleteRecipe(@PathVariable Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rezept nicht gefunden");
        }

        recipeRepository.deleteById(id);
    }
}
