package com.web.billim.client.iamport;

import com.web.billim.client.iamport.response.IamPortAccessTokenResponse;
import com.web.billim.client.iamport.response.IamPortPaymentData;
import com.web.billim.client.iamport.response.IamPortPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class IamPortClient {

	private final RestTemplate restTemplate;
	private final String IAM_PORT_BASE_URL = "https://api.iamport.kr";

	@Value("${import.imp_key}")
	private String impKey;

	@Value("${import.imp_secret}")
	private String impSecret;

	public String getAccessToken() {
		Map<String, String> body = new HashMap<>();
		body.put("imp_key", impKey);
		body.put("imp_secret", impSecret);
		ResponseEntity<IamPortAccessTokenResponse> result = restTemplate.postForEntity(IAM_PORT_BASE_URL + "/users/getToken", body, IamPortAccessTokenResponse.class);
		if (result.getStatusCode().is2xxSuccessful()) {
			return Objects.requireNonNull(result.getBody()).getResponse().getAccessToken();
		}
		throw new RuntimeException("IamPort AccessToken 조회에 실패했습니다!");
	}

	public IamPortPaymentData retrievePaymentHistory(String impUid) {
		String accessToken = this.getAccessToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		ResponseEntity<IamPortPaymentResponse> result = restTemplate.exchange(IAM_PORT_BASE_URL + "/payments/" + impUid, HttpMethod.GET, entity, IamPortPaymentResponse.class);
		if (result.getStatusCode().is2xxSuccessful()) {
			return Objects.requireNonNull(result.getBody()).getResponse();
		}
		throw new RuntimeException("IamPort 결제내역 조회에 실패했습니다!");
	}

}

/*
	결제 버튼 누르기
	1. 서버 갔다와서 (ajax 호출)
	2. 나온 결과로 IamPort API 호출

	fun() {
    	1. ajax 서버 호출 (/order)
    	2. ajax success callback 에서 서버에서 준 data 를 활용해서 IamPort API 호출
	}
*/