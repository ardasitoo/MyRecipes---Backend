package de.htw_berlin.myrecipes;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return List.of(
                new Recipe("Lasagne"),
                new Recipe("Yaglama"),
                new Recipe("Pizza Diavola"),
                new Recipe("Hausgemachter Döner")
        );
    }
}
