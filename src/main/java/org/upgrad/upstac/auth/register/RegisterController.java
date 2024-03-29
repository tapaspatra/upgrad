package org.upgrad.upstac.auth.register;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.users.User;

@RestController
public class RegisterController {

	private RegisterService registerService;

	@Autowired
	public RegisterController(RegisterService userService) {

		this.registerService = userService;
	}

	@RequestMapping(value = "/auth/register", method = RequestMethod.POST)
	public User saveUser(@RequestBody RegisterRequest user) {

		try {
			return registerService.addUser(user);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}

	}

	@RequestMapping(value = "/auth/doctor/register", method = RequestMethod.POST)
	public User saveDoctor(@RequestBody RegisterRequest user) {

		try {
			return registerService.addDoctor(user);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}

	@RequestMapping(value = "/auth/tester/register", method = RequestMethod.POST)
	public User saveTester(@RequestBody RegisterRequest user) {

		try {
			return registerService.addTester(user);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}
}
