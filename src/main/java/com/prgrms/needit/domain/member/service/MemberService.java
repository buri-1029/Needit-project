package com.prgrms.needit.domain.member.service;

import com.prgrms.needit.common.domain.dto.IsUniqueResponse;
import com.prgrms.needit.common.error.ErrorCode;
import com.prgrms.needit.common.error.exception.NotFoundMemberException;
import com.prgrms.needit.domain.member.dto.MemberRequest;
import com.prgrms.needit.domain.member.dto.MemberResponse;
import com.prgrms.needit.domain.member.dto.MemberSelfResponse;
import com.prgrms.needit.domain.member.entity.Member;
import com.prgrms.needit.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(
		MemberRepository memberRepository,
		PasswordEncoder passwordEncoder
	) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public Long createMember(MemberRequest memberRequest) {
		// email 보내고, emailCode 저장
		// 저장된 emailCode와 맞는지 확인
		return memberRepository
			.save(memberRequest.toEntity(passwordEncoder.encode(memberRequest.getPassword())))
			.getId();
	}

	@Transactional(readOnly = true)
	public MemberSelfResponse getMember(Long memberId) {
		return new MemberSelfResponse(findActiveMember(memberId));
	}

	@Transactional(readOnly = true)
	public MemberResponse getOtherMember(Long memberId) {
		return new MemberResponse(findActiveMember(memberId));
	}

	@Transactional
	public Long updateMember(Long memberId, MemberRequest request) {
		Member activeMember = findActiveMember(memberId);
		activeMember.changeMemberInfo(
			request.getEmail(),
			passwordEncoder.encode(request.getPassword()),
			request.getNickname(),
			request.getContact(),
			request.getAddress(),
			request.getProfileImageUrl()
		);
		return activeMember.getId();
	}

	@Transactional
	public void deleteMember(Long memberId) {
		Member activeMember = findActiveMember(memberId);
		activeMember.deleteEntity();
	}

	@Transactional
	public IsUniqueResponse checkEmailIsUnique(String email) {
		return new IsUniqueResponse(!memberRepository.existsByEmail(email));
	}

	@Transactional
	public IsUniqueResponse checkNicknameIsUnique(String nickname) {
		return new IsUniqueResponse(!memberRepository.existsByNickname(nickname));
	}

	@Transactional(readOnly = true)
	public Member findActiveMember(Long memberId) {
		return memberRepository
			.findByIdAndIsDeletedFalse(memberId)
			.orElseThrow(
				() -> new NotFoundMemberException(ErrorCode.NOT_FOUND_MEMBER));
	}
}
