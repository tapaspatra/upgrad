package org.upgrad.upstac.testrequests.flow;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.TestRequestQueryService;

@RestController
@RequestMapping("/api/testrequests")
public class TestRequestFlowController {

	private static final Logger log = LoggerFactory.getLogger(TestRequestFlowController.class);

	@Autowired
	TestRequestFlowService testRequestFlowService;

	@Autowired
	UserLoggedInService userLoggedInService;

	@Autowired
	TestRequestQueryService testRequestQueryService;

	@PreAuthorize("hasAnyRole('TESTER','USER', 'DOCTOR')")
	@GetMapping("/flow/{id}")
	public List<TestRequestFlow> findByRequestId(@PathVariable Long id) {

		TestRequest testRequest = testRequestQueryService.getTestRequestById(id)
				.orElseThrow(() -> new AppException("Invalid Request"));
		;

		return testRequestFlowService.findByRequest(testRequest);
	}

}
