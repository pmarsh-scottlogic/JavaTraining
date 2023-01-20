package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.authInfo.AuthRequest;
import com.example.demo.security.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// ideally we'd not run the whole app, and only have the matcher controller, without even the security layer.
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginValidationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private TradeService tradeService;

    @MockBean
    private Matcher matcher;


    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();
    }

    @Test
    public void ifShouldAlertIfUsernameIsTooShort() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("", "testPassword!"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("length must be between");
        assertThat(result.getResponse().getContentAsString()).contains("username");
    }

    @Test
    public void ifShouldAlertIfUsernameIsTooLong() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("ThisUsernameIsReeeeeeeeeeeeeeeeeaaaallyLong", "testPassword!"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("length must be between");
        assertThat(result.getResponse().getContentAsString()).contains("username");
    }

    @Test
    public void ifShouldAlertIfPasswordTooShort() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("testUsername", "aA!1"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("Password should be");
        assertThat(result.getResponse().getContentAsString()).contains("password");
    }

    @Test
    public void ifShouldAlertIfPasswordTooLong() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("testUsername", "aA!1eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("Password should be");
        assertThat(result.getResponse().getContentAsString()).contains("password");
    }

    @Test
    public void ifShouldAlertIfPasswordMissingUpperCase() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("testUsername", "almostagoodpassword1!"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getResponse().getContentAsString()).contains("Password should be");
        assertThat(result.getResponse().getContentAsString()).contains("password");
    }

    // etc etc
}
