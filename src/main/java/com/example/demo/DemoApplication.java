package com.example.demo;

import com.example.demo.matcher.InitialiseFakeMarket;
import com.example.demo.matcher.Matcher;
import com.example.demo.matcher.models.OrderObj;
import com.example.demo.matcher.models.OrderAction;
import com.example.demo.matcher.services.OrderRepoHandler;
import com.example.demo.security.service.UserService;
import com.example.demo.security.userInfo.AppUser;
import com.example.demo.security.userInfo.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.ArrayList;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean// this bean runs a code block exactly once upon initialisation of the program
	CommandLineRunner run (UserService userService, Matcher matcher, OrderRepoHandler orderRepoHandler) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser(new AppUser(null, "James Lewis", "James", "1GoodPassw0rd?", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Dave Daveman", "DD", "1GoodPassw0rd?", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Jimmy LaCroix", "Jim", "1GoodPassw0rd?", new ArrayList<>()));
			userService.saveUser(new AppUser(null, "Emerald Smelter", "Em", "1GoodPassw0rd?", new ArrayList<>()));

			userService.addRoleToUser("James", "ROLE_ADMIN");
			userService.addRoleToUser("DD", "ROLE_USER");
			userService.addRoleToUser("Jim", "ROLE_USER");
			userService.addRoleToUser("Em", "ROLE_MANAGER");

			orderRepoHandler.saveOrder(new OrderObj("James", new BigDecimal(1), new BigDecimal(1), OrderAction.SELL));

			InitialiseFakeMarket.fillMatcher(matcher);
		};
	}
}
