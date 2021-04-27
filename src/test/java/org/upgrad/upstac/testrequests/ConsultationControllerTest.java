package org.upgrad.upstac.testrequests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ConsultationControllerTest {

	@Autowired
	ConsultationController consultationController;

	@Autowired
	TestRequestQueryService testRequestQueryService;

	@Test
	@WithUserDetails(value = "doctor")
	public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);

		TestRequest testRequestConsulation = new TestRequest();
		testRequestConsulation = consultationController.assignForConsultation(testRequest.getRequestId());
		
		assertThat("the request ids of both the objects created should be same", testRequest.getRequestId() == testRequestConsulation.getRequestId());
		assertThat("the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'", testRequestConsulation.getStatus().equals(RequestStatus.DIAGNOSIS_IN_PROCESS));
		
		
		assertNotNull(testRequestConsulation != null, "testRequestConsulation object value is not null");
		
		testRequestConsulation.getConsultation();
	}

	public TestRequest getTestRequestByStatus(RequestStatus status) {
		return testRequestQueryService.findBy(status).stream().findFirst().get();
	}

	@Test
	@WithUserDetails(value = "doctor")
	public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception() {

		Long InvalidRequestId = -34L;

		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			consultationController.assignForConsultation(InvalidRequestId);
		});
		
		assertThat("Invalid ID", result.getReason().equals("Invalid ID"));
	}

	@Test
	@WithUserDetails(value = "doctor")
	public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

		CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
		
		TestRequest testRequestUpdate = consultationController.updateConsultation(testRequest.getRequestId(), createConsultationRequest);
		testRequestUpdate.setStatus(RequestStatus.COMPLETED);
		
		assertThat("The request ids of both the objects created should be same", testRequest.getRequestId() == testRequestUpdate.getRequestId());
		assertThat("The status of the second object should be equal to 'COMPLETED'", testRequestUpdate.getStatus() == RequestStatus.COMPLETED);
		assertThat("The suggestion of both the objects created should be same", testRequest.getLabResult().getResult() == testRequestUpdate.getLabResult().getResult());
	}

	@Test
	@WithUserDetails(value = "doctor")
	public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

		CreateConsultationRequest CreateConsultationRequest = getCreateConsultationRequest(testRequest);
		
		
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			consultationController.updateConsultation(testRequest.getRequestId(), CreateConsultationRequest);
		});
		
		assertThat("Invalid ID", result.getReason().equals("Invalid ID"));
	}

	@Test
	@WithUserDetails(value = "doctor")
	public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception() {

		TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
		
		CreateConsultationRequest CreateConsultationRequest = getCreateConsultationRequest(testRequest);
		CreateConsultationRequest.setSuggestion(null);
		
		ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
			consultationController.updateConsultation(testRequest.getRequestId(), CreateConsultationRequest);
		});
		

	}

	public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {

		CreateLabResult createLabResult = new CreateLabResult();
		createLabResult.setBloodPressure("100");
		createLabResult.setComments("Negative");
		createLabResult.setHeartBeat("90");
		createLabResult.setTemperature("79");
		createLabResult.setResult(TestStatus.POSITIVE);
	
		CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
		
		if(createLabResult.getResult() == TestStatus.POSITIVE) {
			createConsultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
			createConsultationRequest.setComments("Stay at Home and do medication");
		}else if(createLabResult.getResult() == TestStatus.NEGATIVE) {
			createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
			createConsultationRequest.setComments("OK");
		}else {
		}
		return createConsultationRequest; 

	}

}