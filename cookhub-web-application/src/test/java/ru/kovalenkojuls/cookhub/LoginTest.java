package ru.kovalenkojuls.cookhub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.kovalenkojuls.cookhub.controllers.MainController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController mainController;

    @Test
    public void shouldReturnHomePage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Добро пожаловать в CookHub!")));
    }

    @Test
    public void shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/recipes"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void shouldLoginAndRedirectToRecipes() throws Exception {
        mockMvc.perform(formLogin().user("admin").password("admin"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/recipes"));
    }


    @Test
    public void shouldGet403Code() throws Exception {
        mockMvc.perform(
                post("/login")
                    .param("username", "admin")
                    .param("password", "bad"))
            .andExpect(status().isForbidden());

    }
}
