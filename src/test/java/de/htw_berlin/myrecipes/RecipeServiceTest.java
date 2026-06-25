package de.htw_berlin.myrecipes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private RecipeService recipeService;

    @Test
    void getAllRecipesFiltersByOwner() {
        when(recipeRepository.findByOwnerNameOrderByIdAsc("Mina")).thenReturn(List.of(recipe(1L, "Salat", "Mina")));

        List<Recipe> result = recipeService.getAllRecipes(" Mina ");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOwnerName()).isEqualTo("Mina");
    }

    @Test
    void getAllRecipesReturnsEveryRecipeWithoutOwnerFilter() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe(1L, "Pasta", "Simar"), recipe(2L, "Curry", "Mina")));

        List<Recipe> result = recipeService.getAllRecipes(null);

        assertThat(result).extracting(Recipe::getName).containsExactly("Pasta", "Curry");
    }

    @Test
    void createRecipeUsesDefaultOwnerWhenMissing() {
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Recipe recipe = recipe(null, "Suppe", null);
        recipe.setOwnerName(" ");

        Recipe result = recipeService.createRecipe(recipe);

        assertThat(result.getOwnerName()).isEqualTo("Simar");
    }

    @Test
    void updateRecipeCopiesRequestFields() {
        Recipe existingRecipe = recipe(1L, "Pasta", "Simar");
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(existingRecipe));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe updatedRecipe = recipe(null, "Pasta Napoli", "Mina");
        updatedRecipe.setFavorite(true);

        Recipe result = recipeService.updateRecipe(1L, updatedRecipe);

        assertThat(result.getName()).isEqualTo("Pasta Napoli");
        assertThat(result.getOwnerName()).isEqualTo("Mina");
        assertThat(result.getFavorite()).isTrue();
    }

    @Test
    void updateRecipeThrowsNotFoundForMissingRecipe() {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(99L, recipe(null, "Pasta", "Simar")))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteRecipeDeletesExistingRecipe() {
        when(recipeRepository.existsById(1L)).thenReturn(true);

        recipeService.deleteRecipe(1L);

        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void createRecipeSavesTrimmedOwner() {
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);

        recipeService.createRecipe(recipe(null, "Pizza", " Familie "));

        verify(recipeRepository).save(captor.capture());
        assertThat(captor.getValue().getOwnerName()).isEqualTo("Familie");
    }

    @Test
    void shareRecipeCreatesCopyForTargetOwner() {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe(1L, "Pasta", "Simar")));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);

        Recipe result = recipeService.shareRecipe(1L, " Familie ");

        verify(recipeRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Pasta");
        assertThat(result.getOwnerName()).isEqualTo("Familie");
        assertThat(result.getFavorite()).isFalse();
    }

    @Test
    void shareRecipeRejectsMissingTargetOwner() {
        assertThatThrownBy(() -> recipeService.shareRecipe(1L, " "))
                .isInstanceOf(ResponseStatusException.class);
    }

    private Recipe recipe(Long id, String name, String ownerName) {
        Recipe recipe = new Recipe(
                name,
                "Schnelles Rezept",
                "Hauptgericht",
                25,
                "Nudeln, Tomaten",
                "Kochen und servieren",
                ownerName);
        ReflectionTestUtils.setField(recipe, "id", id);
        return recipe;
    }
}
