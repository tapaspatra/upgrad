package org.upgrad.upstac.testrequests.lab;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;
import static org.upgrad.upstac.exception.UpgradResponseStatusException.asConstraintViolation;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

/**
 * This class is responsible to handle all request for URL /api/labrequests
 * @author TAPAS
 *
 */
@RestController
@RequestMapping("/api/labrequests")
public class LabRequestController {

	Logger log = LoggerFactory.getLogger(LabRequestController.class);

	@Autowired
	private TestRequestUpdateService testRequestUpdateService;

	@Autowired
	private TestRequestQueryService testRequestQueryService;

	@Autowired
	private UserLoggedInService userLoggedInService;

	@GetMapping("/to-be-tested")
	@PreAuthorize("hasAnyRole('TESTER')")
	public List<TestRequest> getForTests() {

		return testRequestQueryService.findBy(RequestStatus.INITIATED);

	}

	/**
	 * This method is used to get the list of Test requests assigned to Tester 
	 * 
	 * @return List<TestRequest>
	 * @throws AppException
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('TESTER')")
	public List<TestRequest> getForTester() {

		try {
			User tester = userLoggedInService.getLoggedInUser();
			List<TestRequest> testRequests = testRequestQueryService.findByTester(tester);
			return testRequests;
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}

	}
	
	/**
	 * This method is used assign the Test request to Tester by request ID
	 * @param id
	 * @return TestRequest
	 */
	@PreAuthorize("hasAnyRole('TESTER')")
	@PutMapping("/assign/{id}")
	public TestRequest assignForLabTest(@PathVariable Long id) {

		User tester = userLoggedInService.getLoggedInUser();

		return testRequestUpdateService.assignForLabTest(id, tester);
	}
	
	/**
	 * This method is used to update the lab test result by Tester
	 * @param id
	 * @param createLabResult
	 * @return TestRequest
	 * @throws ConstraintViolationException,  AppException
	 */
	@PreAuthorize("hasAnyRole('TESTER')")
	@PutMapping("/update/{id}")
	public TestRequest updateLabTest(@PathVariable Long id, @RequestBody CreateLabResult createLabResult) {

		try {

			User tester = userLoggedInService.getLoggedInUser();
			return testRequestUpdateService.updateLabTest(id, createLabResult, tester);

		} catch (ConstraintViolationException e) {
			throw asConstraintViolation(e);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}

}
