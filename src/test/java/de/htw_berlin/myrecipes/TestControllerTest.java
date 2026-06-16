package de.htw_berlin.myrecipes;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeRepository recipeRepository;

    @Test
    void getAllRecipesReturnsRecipesAsJson() throws Exception {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe(1L, "Pasta"), recipe(2L, "Salat")));

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Pasta")))
                .andExpect(jsonPath("$[1].name", is("Salat")));
    }

    @Test
    void getRecipeReturnsRecipeDetails() throws Exception {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe(1L, "Pasta")));

        mockMvc.perform(get("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Pasta")))
                .andExpect(jsonPath("$.category", is("Hauptgericht")))
                .andExpect(jsonPath("$.preparationTime", is(25)));
    }

    @Test
    void getRecipeReturnsNotFoundForMissingRecipe() throws Exception {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/recipes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRecipeReturnsSavedRecipeAsJson() throws Exception {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe(7L, "Pizza"));

        mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pizza")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.name", is("Pizza")));
    }

    @Test
    void createRecipeSavesAllRequestFields() throws Exception {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe(1L, "Pizza"));
        ArgumentCaptor<Recipe> recipeCaptor = ArgumentCaptor.forClass(Recipe.class);

        mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pizza")));

        verify(recipeRepository).save(recipeCaptor.capture());
        org.assertj.core.api.Assertions.assertThat(recipeCaptor.getValue().getName()).isEqualTo("Pizza");
        org.assertj.core.api.Assertions.assertThat(recipeCaptor.getValue().getCategory()).isEqualTo("Hauptgericht");
        org.assertj.core.api.Assertions.assertThat(recipeCaptor.getValue().getIngredients()).contains("Tomaten");
    }

    @Test
    void updateRecipeChangesExistingRecipe() throws Exception {
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe(1L, "Pasta")));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pasta Napoli")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Pasta Napoli")));
    }

    @Test
    void updateRecipeReturnsNotFoundForMissingRecipe() throws Exception {
        when(recipeRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/recipes/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pasta Napoli")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRecipeDeletesExistingRecipe() throws Exception {
        when(recipeRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/recipes/1"))
                .andExpect(status().isOk());

        verify(recipeRepository).deleteById(1L);
    }

    @Test
    void deleteRecipeReturnsNotFoundForMissingRecipe() throws Exception {
        when(recipeRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/recipes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void corsPreflightAllowsFrontendRequests() throws Exception {
        mockMvc.perform(options("/recipes")
                .header("Origin", "https://example-frontend.onrender.com")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }

    private Recipe recipe(Long id, String name) {
        Recipe recipe = new Recipe(
                name,
                "Schnelles Rezept",
                "Hauptgericht",
                25,
                "Nudeln, Tomaten",
                "Kochen und servieren");
        ReflectionTestUtils.setField(recipe, "id", id);
        return recipe;
    }

    private String recipeJson(String name) {
        return """
                {
                  "name": "%s",
                  "description": "Schnelles Rezept",
                  "category": "Hauptgericht",
                  "preparationTime": 25,
                  "ingredients": "Nudeln, Tomaten",
                  "steps": "Kochen und servieren"
                }
                """.formatted(name);
    }
}
