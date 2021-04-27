package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.core.Is;

@SpringBootTest
@Slf4j
class LabRequestControllerTest {

	@Autowired
	LabRequestController labRequestController;

	@Autowired
	TestRequestQueryService testRequestQueryService;

	@Test
	@WithUserDetails(value = "tester")
	public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);
		
		TestRequest testRequestLab = labRequestController.assignForLabTest(testRequest.getRequestId());
		
		assertThat("The request ids of both the objects created should be same", testRequest.getRequestId() == testRequestLab.getRequestId() );
		assertThat("The status of the second object should be equal to 'INITIATED'", testRequest.getStatus().equals(RequestStatus.INITIATED) );
		
		assertNotNull(testRequestLab.getLabResult() != null, "object is not null");
		
		testRequestLab.getLabResult();

	}

	public TestRequest getTestRequestByStatus(RequestStatus status) {
		return testRequestQueryService.findBy(status).stream().findFirst().get();
	}

	@Test
	@WithUserDetails(value = "tester")
	public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception() {

		Long InvalidRequestId = -34L;

		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			labRequestController.assignForLabTest(InvalidRequestId);
		});
		
		assertThat("Invalid ID", result.getReason().equals("Invalid ID"));
		
	}

	@Test
	@WithUserDetails(value = "tester")
	public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

		CreateLabResult CreateLabResult = getCreateLabResult(testRequest);
		
		TestRequest testRequestUpdate = new TestRequest();
		testRequestUpdate.setStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
		
		labRequestController.updateLabTest(testRequest.getRequestId(), CreateLabResult);
		
		assertThat("The request ids of both the objects created should be same", testRequest.getRequestId() == testRequestUpdate.getRequestId() );
		assertThat("The status of the second object should be equal to 'LAB_TEST_COMPLETED'", testRequest.getStatus() == RequestStatus.LAB_TEST_COMPLETED);
		assertThat("The status of the second object should be equal to 'LAB_TEST_COMPLETED'", testRequest.getLabResult().getResult().equals(testRequestUpdate.getLabResult().getResult()));

	}

	@Test
	@WithUserDetails(value = "tester")
	public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
		
		CreateLabResult CreateLabResult = getCreateLabResult(testRequest);
		
		
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			labRequestController.updateLabTest(-32L, CreateLabResult);
		});
		
		assertThat("Invalid ID", result.getReason().equals("Invalid ID"));
	}

	@Test
	@WithUserDetails(value = "tester")
	public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

		CreateLabResult CreateLabResult = getCreateLabResult(testRequest);
		
		
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			labRequestController.updateLabTest(testRequest.getRequestId(), CreateLabResult);
		});
		
		assertThat("ConstraintViolationException Found", result.getReason().equals("ConstraintViolationException"));
	}

	public CreateLabResult getCreateLabResult(TestRequest testRequest) {

		CreateLabResult createLabResult = new CreateLabResult();
		createLabResult.setBloodPressure("100");
		createLabResult.setComments("Negative");
		createLabResult.setHeartBeat("90");
		createLabResult.setTemperature("79");
		createLabResult.setResult(TestStatus.POSITIVE);
	

		return createLabResult; 
	}

}