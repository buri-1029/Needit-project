package com.prgrms.needit.domain.board.donation.service;

import com.prgrms.needit.common.domain.dto.DealStatusRequest;
import com.prgrms.needit.common.domain.entity.ThemeTag;
import com.prgrms.needit.common.domain.repository.ThemeTagRepository;
import com.prgrms.needit.common.enums.DonationStatus;
import com.prgrms.needit.common.error.ErrorCode;
import com.prgrms.needit.common.error.exception.NotFoundResourceException;
import com.prgrms.needit.common.error.exception.NotMatchWriterException;
import com.prgrms.needit.domain.board.donation.dto.DonationFilterRequest;
import com.prgrms.needit.domain.board.donation.dto.DonationRequest;
import com.prgrms.needit.domain.board.donation.dto.DonationResponse;
import com.prgrms.needit.domain.board.donation.entity.Donation;
import com.prgrms.needit.domain.board.donation.repository.DonationRepository;
import com.prgrms.needit.domain.board.donation.repository.DonationTagRepository;
import com.prgrms.needit.domain.member.entity.Member;
import com.prgrms.needit.domain.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationService {

	private final MemberRepository memberRepository;
	private final DonationRepository donationRepository;
	private final ThemeTagRepository themeTagRepository;
	private final DonationTagRepository donationTagRepository;

	public DonationService(
		MemberRepository memberRepository,
		DonationRepository donationRepository,
		ThemeTagRepository themeTagRepository,
		DonationTagRepository donationTagRepository
	) {
		this.memberRepository = memberRepository;
		this.donationRepository = donationRepository;
		this.themeTagRepository = themeTagRepository;
		this.donationTagRepository = donationTagRepository;
	}

	@Transactional(readOnly = true)
	public Page<DonationResponse> getDonations(
		DonationFilterRequest request, Pageable pageable
	) {
		return donationRepository.searchAllByFilter(request, pageable)
								 .map(DonationResponse::new);
	}

	@Transactional(readOnly = true)
	public DonationResponse getDonation(Long id) {
		return new DonationResponse(findActiveDonation(id));
	}

	@Transactional
	public Long registerDonation(DonationRequest request) {
		Member member = memberRepository.findById(1L)
										.get();

		Donation donation = request.toEntity();
		donation.addMember(member);

		registerTag(request, donation);

		return donationRepository
			.save(donation)
			.getId();
	}

	@Transactional
	public Long modifyDonation(Long id, DonationRequest request) {
		Member member = memberRepository.findById(1L)
										.get();

		Donation donation = findActiveDonation(id);
		checkWriter(member, donation);

		donation.changeInfo(request);
		donationTagRepository.deleteAllByDonation(donation);
		registerTag(request, donation);

		return donation.getId();
	}

	@Transactional
	public Long modifyDealStatus(Long id, DealStatusRequest request) {
		Member member = memberRepository.findById(1L)
										.get();

		Donation donation = findActiveDonation(id);
		checkWriter(member, donation);

		donation.changeStatus(DonationStatus.of(request.getStatus()));

		return donation.getId();
	}

	@Transactional
	public void removeDonation(Long id) {
		Member member = memberRepository.findById(1L)
										.get();

		Donation donation = findActiveDonation(id);
		checkWriter(member, donation);

		donation.deleteEntity();
	}

	@Transactional(readOnly = true)
	public Donation findActiveDonation(Long id) {
		return donationRepository
			.findByIdAndIsDeletedFalse(id)
			.orElseThrow(() -> new NotFoundResourceException(ErrorCode.NOT_FOUND_DONATION));
	}

	private void registerTag(DonationRequest request, Donation donation) {
		for (Long tagId : request.getTags()) {
			ThemeTag themeTag = themeTagRepository.findById(tagId)
												  .get();
			donation.addTag(themeTag);
		}
	}

	private void checkWriter(Member member, Donation donation) {
		if (!donation.getMember()
					 .equals(member)) {
			throw new NotMatchWriterException(ErrorCode.NOT_MATCH_WRITER);
		}
	}
}

