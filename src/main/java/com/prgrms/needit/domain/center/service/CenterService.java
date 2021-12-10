package com.prgrms.needit.domain.center.service;

import com.prgrms.needit.common.domain.dto.IsUniqueResponse;
import com.prgrms.needit.common.error.ErrorCode;
import com.prgrms.needit.common.error.exception.NotFoundCenterException;
import com.prgrms.needit.domain.center.dto.CenterRequest;
import com.prgrms.needit.domain.center.dto.CenterResponse;
import com.prgrms.needit.domain.center.dto.CenterSelfResponse;
import com.prgrms.needit.domain.center.entity.Center;
import com.prgrms.needit.domain.center.repository.CenterRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CenterService {

	private final CenterRepository centerRepository;
	private final PasswordEncoder passwordEncoder;

	public CenterService(
		CenterRepository centerRepository,
		PasswordEncoder passwordEncoder
	) {
		this.centerRepository = centerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Long createCenter(CenterRequest centerRequest) {
		return centerRepository
			.save(centerRequest.toEntity(passwordEncoder.encode(centerRequest.getPassword())))
			.getId();
	}

	@Transactional(readOnly = true)
	public CenterSelfResponse getCenter(Long centerId) {
		return new CenterSelfResponse(findActiveCenter(centerId));
	}

	@Transactional(readOnly = true)
	public CenterResponse getOtherCenter(Long centerId) {
		return new CenterResponse(findActiveCenter(centerId));
	}

	@Transactional
	public Long updateCenter(Long centerId, CenterRequest request) {
		Center activeCenter = findActiveCenter(centerId);
		activeCenter.changeCenterInfo(
			request.getEmail(),
			passwordEncoder.encode(request.getPassword()),
			request.getName(),
			request.getContact(),
			request.getAddress(),
			request.getProfileImageUrl(),
			request.getOwner()
		);
		return activeCenter.getId();
	}

	@Transactional
	public void deleteCenter(Long centerId) {
		Center activeCenter = findActiveCenter(centerId);
		activeCenter.deleteEntity();
	}

	@Transactional(readOnly = true)
	public Center findActiveCenter(Long centerId) {
		return centerRepository
			.findByIdAndIsDeletedFalse(centerId)
			.orElseThrow(
				() -> new NotFoundCenterException(ErrorCode.NOT_FOUND_CENTER));
	}

	@Transactional
	public IsUniqueResponse checkEmailIsUnique(String email) {
		return new IsUniqueResponse(!centerRepository.existsByEmail(email));
	}

	@Transactional
	public IsUniqueResponse checkNameIsUnique(String name) {
		return new IsUniqueResponse(!centerRepository.existsByName(name));
	}
}
