package com.prgrms.needit.domain.contract.exception;

import com.prgrms.needit.common.error.ErrorCode;
import com.prgrms.needit.common.error.exception.CustomException;
import lombok.Getter;

@Getter
public class ContractNotFoundException extends CustomException {

	public ContractNotFoundException() {
		super(ErrorCode.NOT_FOUND_CONTRACT);
	}

}
