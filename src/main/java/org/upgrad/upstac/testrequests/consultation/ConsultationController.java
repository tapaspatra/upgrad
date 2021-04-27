package org.upgrad.upstac.testrequests.consultation;

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
import org.upgrad.upstac.testrequests.TestRequestService;
import org.upgrad.upstac.testrequests.TestRequestUpdateService;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.users.User;

/**
 * This class is responsible to handle all request for URL /api/consultations
 * 
 * @author TAPAS
 *
 */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

	Logger log = LoggerFactory.getLogger(ConsultationController.class);

	@Autowired
	private TestRequestUpdateService testRequestUpdateService;

	@Autowired
	private TestRequestQueryService testRequestQueryService;

	@Autowired
	TestRequestFlowService testRequestFlowService;

	@Autowired
	private UserLoggedInService userLoggedInService;

	@Autowired
	TestRequestService testRequestService;
	
	/**
	 * This method is used to get all the test request by Test status LAB_TEST_COMPLETED
	 * @return List<TestRequest
	 * @throws AppException
	 */
	@GetMapping("/in-queue")
	@PreAuthorize("hasAnyRole('DOCTOR')")
	public List<TestRequest> getForConsultations() {

		try {
			List<TestRequest> testRequests = testRequestQueryService.findBy(RequestStatus.LAB_TEST_COMPLETED);

			return testRequests;
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}

	}
	
	/**
	 * This method is used find all the request assign to logged in doctor
	 * @return List<TestRequest>
	 * @throws AppException
	 */
	@GetMapping
	@PreAuthorize("hasAnyRole('DOCTOR')")
	public List<TestRequest> getForDoctor() {

		try {
			User doctor = userLoggedInService.getLoggedInUser();
			List<TestRequest> testRequests = testRequestQueryService.findByDoctor(doctor);

			return testRequests;
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}

	}
	
	/**
	 * This method is used to assign the request id for consolation
	 * @param id
	 * @return TestRequest
	 * @throws AppException
	 */
	@PreAuthorize("hasAnyRole('DOCTOR')")
	@PutMapping("/assign/{id}")
	public TestRequest assignForConsultation(@PathVariable Long id) {

		try {
			User doctor = userLoggedInService.getLoggedInUser();
			TestRequest testRequest = testRequestUpdateService.assignForConsultation(id, doctor);
			return testRequest;

		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}
	
	/**
	 * This method is used update the consulation for each request assign to doctor
	 * @param id
	 * @param createConsultationRequest
	 * @return TestRequest
	 * @throws ConstraintViolationException, AppException
	 */
	@PreAuthorize("hasAnyRole('DOCTOR')")
	@PutMapping("/update/{id}")
	public TestRequest updateConsultation(@PathVariable Long id,
			@RequestBody CreateConsultationRequest createConsultationRequest) {

		try {
			User doctor = userLoggedInService.getLoggedInUser();
			TestRequest testRequest = testRequestUpdateService.updateConsultation(id, createConsultationRequest, doctor);
			return testRequest;
		} catch (ConstraintViolationException e) {
			throw asConstraintViolation(e);
		} catch (AppException e) {
			throw asBadRequest(e.getMessage());
		}
	}

}
