package de.htw_berlin.myrecipes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private AppUserService appUserService;

    @Test
    void getUsersReturnsSortedUsersFromRepository() {
        when(appUserRepository.findAllByOrderByNameAsc()).thenReturn(List.of(new AppUser("Dozent")));

        List<AppUser> result = appUserService.getUsers();

        assertThat(result).extracting(AppUser::getName).containsExactly("Dozent");
    }

    @Test
    void createUserTrimsNameBeforeSaving() {
        when(appUserRepository.existsByNameIgnoreCase("Mina")).thenReturn(false);
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.createUser(new AppUser(" Mina "));

        assertThat(result.getName()).isEqualTo("Mina");
    }

    @Test
    void createUserRejectsDuplicateName() {
        when(appUserRepository.existsByNameIgnoreCase("Mina")).thenReturn(true);

        assertThatThrownBy(() -> appUserService.createUser(new AppUser("Mina")))
                .isInstanceOf(ResponseStatusException.class);
    }
}
