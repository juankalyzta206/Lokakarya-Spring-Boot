package com.ogya.lokakarya.exercise.feign.repository.usermanagement;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ogya.lokakarya.exercise.feign.request.usermanagement.UsersFeignToWebServiceRequest;
import com.ogya.lokakarya.exercise.feign.response.usermanagement.UsersFeignResponse;

@FeignClient(value = "user-role", url = "https://simple-rest-production.up.railway.app/")
public interface UsersFeignRepository {

	@RequestMapping(method = RequestMethod.GET, value = "/user-role/inquiry/{name}")
	public UsersFeignResponse userRoleInquiry(@PathVariable("name") String name);

	@RequestMapping(method = RequestMethod.POST, value = "/user-role/record")
	public UsersFeignResponse userRoleRecord(@RequestBody UsersFeignToWebServiceRequest request);
}
