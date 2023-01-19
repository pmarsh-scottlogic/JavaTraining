package com.example.demo;

import com.example.demo.security.authInfo.AuthRequest;
import com.example.demo.security.filter.JwtTokenFilter;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserService userService;

    private MockMvc mvc;

    private AppUser testUser1;
    private AppUser testUser2;
    @BeforeEach
    public void setup() throws Exception {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).apply(springSecurity()).build();

        testUser1 = new AppUser("5bbf6bc6-517d-46a4-b908-b01ad3b75c15", "testName1", "testUsername1", "testPassword1", new ArrayList<>());
        testUser2 = new AppUser("503b3c07-2239-4fa7-bc9d-c940c2005f10", "testName2", "testUsername2", "testPassword2", new ArrayList<>());
        // these guys will need accountIds

        userService.saveUser(testUser1);
        userService.saveUser(testUser2);
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
        String requestBody = TestUtils.asJsonString(new AuthRequest("badUsername", "badPassword"));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowPrivateAccessWithoutToken() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1.getId()))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowPrivateAccessWithBadlyFormattedToken() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1.getId())
                                .header("Authorization", "Bearer badtoken"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldNotAllowPrivateAccessWithOldToken() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/private/orderbook/buy/" + testUser1.getId())
                                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    public void itShouldAllowAccessToPublicBuyOrderbook() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/buy"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicSellOrderbook() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/sell"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicBuyOrderdepth() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/depth/buy"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToPublicSellOrderdepth() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/orderbook/depth/sell"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldAllowAccessToTrades() throws Exception {
        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.get("/public/tradebook"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void itShouldReturnTokenOnSuccessfulLogin() throws Exception {
        String requestBody = TestUtils.asJsonString(new AuthRequest(testUser1.getUsername(), testUser2.getPassword()));

        MvcResult result = mvc.perform(
                        MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.getResponse().getContentAsString()).matches("[w-]*.[w-]*.[w-]*");
    }

    @Test
    public void itShouldAllowPrivateAccessToBuyOrdersWithValidJWT() throws Exception {
        // placeholder
        assertThat(true).isFalse();
    }

    @Test
    public void itShouldAllowPrivateAccessToSellOrdersWithValidJWT() throws Exception {
        // placeholder
        assertThat(true).isFalse();
    }

    @Test
    public void itShouldNotAllowPrivateAccessToBuyOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        assertThat(true).isFalse();
    }

    @Test
    public void itShouldNotAllowPrivateAccessToSellOrdersWithValidJWTButWrongAccount() throws Exception {
        // placeholder
        assertThat(true).isFalse();
    }

}
