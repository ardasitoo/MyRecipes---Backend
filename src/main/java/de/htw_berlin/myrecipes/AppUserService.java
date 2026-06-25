package de.htw_berlin.myrecipes;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public List<AppUser> getUsers() {
        return appUserRepository.findAllByOrderByNameAsc();
    }

    public AppUser createUser(AppUser appUser) {
        if (appUser.getName() == null || appUser.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Benutzer braucht einen Namen");
        }

        String name = appUser.getName().trim();

        if (appUserRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzer existiert bereits");
        }

        appUser.setName(name);
        return appUserRepository.save(appUser);
    }
}
