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

@WebMvcTest(AppUserController.class)
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppUserService appUserService;

    @Test
    void getUsersReturnsUsersAsJson() throws Exception {
        when(appUserService.getUsers()).thenReturn(List.of(new AppUser("Simar"), new AppUser("Familie")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Simar")));
    }

    @Test
    void createUserReturnsSavedUser() throws Exception {
        when(appUserService.createUser(any(AppUser.class))).thenReturn(new AppUser("Mina"));

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Mina"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Mina")));
    }

    @Test
    void deleteUserDelegatesToService() throws Exception {
        mockMvc.perform(delete("/users/2"))
                .andExpect(status().isOk());

        verify(appUserService).deleteUser(2L);
    }
}
