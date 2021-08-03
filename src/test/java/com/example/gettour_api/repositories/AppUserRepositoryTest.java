package com.example.gettour_api.repositories;

import com.example.gettour_api.models.AppUser;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void findByEmail() {

        String email = "shafig@gmail.com";

        AppUser user = new AppUser();
        user.setEmail(email);
        user.setPassword("pass");
        user.setAgentName("Shafig");
        user.setCompanyName("MyTour");
        user.setEnabled(false);
        user.setLocked(false);

        appUserRepository.save(user);

        AppUser expectedUser = appUserRepository.findByEmail(email).get();

        AssertionsForInterfaceTypes.assertThat(expectedUser).isEqualTo(user);
    }

    @Test
    void findAppUserById() {
    }
}