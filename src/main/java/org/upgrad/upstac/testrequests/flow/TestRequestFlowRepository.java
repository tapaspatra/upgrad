package org.upgrad.upstac.testrequests.flow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.upgrad.upstac.testrequests.TestRequest;

public interface TestRequestFlowRepository extends JpaRepository<TestRequestFlow, Long> {

	void deleteById(Long id);

	List<TestRequestFlow> findByRequest(TestRequest request);

}
