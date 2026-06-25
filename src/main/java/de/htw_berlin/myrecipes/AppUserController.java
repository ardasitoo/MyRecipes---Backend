package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/users")
    public List<AppUser> getUsers() {
        return appUserService.getUsers();
    }

    @PostMapping("/users")
    public AppUser createUser(@RequestBody AppUser appUser) {
        return appUserService.createUser(appUser);
    }
}
