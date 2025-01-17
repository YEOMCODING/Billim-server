package com.web.billim.common.exception;

import org.springframework.security.core.AuthenticationException;

import com.web.billim.common.exception.handler.ErrorCode;

public abstract class AuthenticationBusinessException extends AuthenticationException {

	private final ErrorCode errorCode;

	public AuthenticationBusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public AuthenticationBusinessException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}

	public AuthenticationBusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

}
