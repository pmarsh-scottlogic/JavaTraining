package com.example.demo.WebAndSecurity;

import com.example.demo.Matcher.TestUtils;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.services.OrderService;
import com.example.demo.matcher.services.TradeService;
import com.example.demo.security.authInfo.AuthRequest;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.*;
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


import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

// these tests require the whole system to be running
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest {
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

    private AppUser testUser1;



    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();

        testUser1 = new AppUser(null, "testName1", "testUsername1", "testPassword1!", new ArrayList<>());
        userService.saveUser(testUser1);
    }

    @Test
    public void itShouldNotAllowLoginWithNoCredentials() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    public void itShouldNotAllowLoginWithBadCredentials() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest("badUsername", "badPassword1!"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldReturnTokenOnSuccessfulLogin() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest(testUser1.getUsername(), "testPassword1!"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        String JWTRegexPattern = "[\\w-]+\\.[\\w-]+\\.[\\w-]+";
        assertThat(result.getResponse().getContentAsString()).containsPattern(JWTRegexPattern);
    }
}
