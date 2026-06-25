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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService recipeService;

    @Test
    void getAllRecipesReturnsRecipesAsJson() throws Exception {
        when(recipeService.getAllRecipes(null)).thenReturn(List.of(recipe(1L, "Pasta"), recipe(2L, "Salat")));

        mockMvc.perform(get("/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Pasta")))
                .andExpect(jsonPath("$[1].name", is("Salat")));
    }

    @Test
    void getAllRecipesPassesOwnerFilterToService() throws Exception {
        when(recipeService.getAllRecipes("Mina")).thenReturn(List.of(recipe(3L, "Curry", "Mina")));

        mockMvc.perform(get("/recipes").param("owner", "Mina"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerName", is("Mina")));

        verify(recipeService).getAllRecipes("Mina");
    }

    @Test
    void getRecipeReturnsRecipeDetails() throws Exception {
        when(recipeService.getRecipe(1L)).thenReturn(recipe(1L, "Pasta"));

        mockMvc.perform(get("/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Pasta")))
                .andExpect(jsonPath("$.category", is("Hauptgericht")))
                .andExpect(jsonPath("$.preparationTime", is(25)));
    }

    @Test
    void createRecipeReturnsSavedRecipeAsJson() throws Exception {
        when(recipeService.createRecipe(any(Recipe.class))).thenReturn(recipe(7L, "Pizza"));

        mockMvc.perform(post("/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pizza", "Simar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.name", is("Pizza")));
    }

    @Test
    void shareRecipeReturnsCopiedRecipeForTargetOwner() throws Exception {
        when(recipeService.shareRecipe(1L, "Familie")).thenReturn(recipe(8L, "Pasta", "Familie"));

        mockMvc.perform(post("/recipes/1/share")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "ownerName": "Familie"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(8)))
                .andExpect(jsonPath("$.ownerName", is("Familie")));
    }

    @Test
    void updateRecipeChangesExistingRecipe() throws Exception {
        when(recipeService.updateRecipe(any(Long.class), any(Recipe.class))).thenReturn(recipe(1L, "Pasta Napoli"));

        mockMvc.perform(put("/recipes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(recipeJson("Pasta Napoli", "Simar")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Pasta Napoli")));
    }

    @Test
    void deleteRecipeDeletesExistingRecipe() throws Exception {
        mockMvc.perform(delete("/recipes/1"))
                .andExpect(status().isOk());

        verify(recipeService).deleteRecipe(1L);
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
        return recipe(id, name, "Simar");
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

    private String recipeJson(String name, String ownerName) {
        return """
                {
                  "name": "%s",
                  "description": "Schnelles Rezept",
                  "category": "Hauptgericht",
                  "preparationTime": 25,
                  "ingredients": "Nudeln, Tomaten",
                  "steps": "Kochen und servieren",
                  "ownerName": "%s"
                }
                """.formatted(name, ownerName);
    }
}
