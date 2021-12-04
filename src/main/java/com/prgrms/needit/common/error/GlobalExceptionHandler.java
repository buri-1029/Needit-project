package com.prgrms.needit.common.error;

import com.prgrms.needit.common.error.exception.InvalidArgumentException;
import com.prgrms.needit.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorResponse> InvalidArgumentExceptionHandler(InvalidArgumentException ex) {
		log.error("Exception : " + ex.getMessage());
		ErrorResponse response = ErrorResponse.of(
			ex.getErrorCode()
		);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception ex) {
		log.error("Exception : " + ex.getMessage());
		ErrorResponse response = ErrorResponse.of(
			ErrorCode.INTERNAL_SERVER_ERROR
		);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
