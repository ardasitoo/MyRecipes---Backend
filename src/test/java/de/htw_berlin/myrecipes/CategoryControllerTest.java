package de.htw_berlin.myrecipes;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void getCategoriesReturnsOwnerCategories() throws Exception {
        when(categoryService.getCategories("Simar")).thenReturn(List.of(new Category("Hauptgericht", "Simar")));

        mockMvc.perform(get("/categories").param("owner", "Simar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Hauptgericht")));
    }

    @Test
    void createCategoryReturnsSavedCategory() throws Exception {
        when(categoryService.createCategory(any(Category.class))).thenReturn(new Category("Dessert", "Simar"));

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Dessert",
                          "ownerName": "Simar"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Dessert")))
                .andExpect(jsonPath("$.ownerName", is("Simar")));
    }

    @Test
    void deleteCategoryDelegatesToService() throws Exception {
        mockMvc.perform(delete("/categories/4"))
                .andExpect(status().isOk());

        verify(categoryService).deleteCategory(4L);
    }
}
