package com.prgrms.needit.notification.service;

import static org.junit.jupiter.api.Assertions.*;

import com.prgrms.needit.common.enums.DonationCategory;
import com.prgrms.needit.common.enums.DonationQuality;
import com.prgrms.needit.common.enums.DonationStatus;
import com.prgrms.needit.common.enums.UserType;
import com.prgrms.needit.domain.board.donation.entity.Donation;
import com.prgrms.needit.domain.board.donation.repository.DonationRepository;
import com.prgrms.needit.domain.board.wish.entity.DonationWish;
import com.prgrms.needit.domain.board.wish.repository.DonationWishRepository;
import com.prgrms.needit.domain.center.entity.Center;
import com.prgrms.needit.domain.center.repository.CenterRepository;
import com.prgrms.needit.domain.member.entity.Member;
import com.prgrms.needit.domain.member.repository.MemberRepository;
import com.prgrms.needit.domain.notification.entity.Notification;
import com.prgrms.needit.domain.notification.entity.enums.NotificationContentType;
import com.prgrms.needit.domain.notification.entity.response.NotificationResponse;
import com.prgrms.needit.domain.notification.repository.NotificationRepository;
import com.prgrms.needit.domain.notification.service.NotificationService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class NotificationServiceTest {

	@Autowired
	NotificationService notificationService;
	@Autowired
	NotificationRepository notificationRepository;
	@Autowired
	CenterRepository centerRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	DonationRepository donationRepository;
	@Autowired
	DonationWishRepository donationWishRepository;

	Center center1, center2;
	Member member;
	DonationWish donationWish1, donationWish2, donationWish3;
	Notification noti1, noti2;

	@BeforeEach
	void init() {
		center1 = centerRepository.save(
			Center.builder()
				  .address("CENTER1_ADDRESS")
				  .name("CENTER1_NAME")
				  .contact("CENTER1_CONTACT")
				  .email("CENTEr1_EMAIL")
				  .owner("CENTER1_OWNER")
				  .password("P")
				  .profileImageUrl("A")
				  .registrationCode("CENTER1_REGI_CODE")
				  .build());
		center2 = centerRepository.save(
			Center.builder()
				  .address("CENTEr2_ADDR")
				  .name("CENTER2_NAME")
				  .contact("CENTEr2_CONTACT")
				  .email("CENTER2_EMAIL")
				  .owner("CENTER2_OWNER")
				  .password("P")
				  .profileImageUrl("A")
				  .registrationCode("CENTER2_REGI_CODE")
				  .build());
		member = memberRepository.save(
			Member.builder()
				  .address("A")
				  .contact("C")
				  .email("MEMBER1_EMAIL")
				  .password("P")
				  .profileImageUrl("AAA")
				  .nickname("MEMBER1_NIckNAME")
				  .build());
		donationWish1 = donationWishRepository.save(
			DonationWish.builder()
						.center(center1)
						.content("CONTENT")
						.category(DonationCategory.GOODS)
						.status(DonationStatus.DONATING)
						.title("TIT")
						.build());
		donationWish2 = donationWishRepository.save(
			DonationWish.builder()
						.center(center1)
						.content("CONTENT")
						.category(DonationCategory.GOODS)
						.status(DonationStatus.DONATING)
						.title("TIT")
						.build());
		donationWish3 = donationWishRepository.save(
			DonationWish.builder()
						.center(center1)
						.content("CONTENT_WISH_3")
						.category(DonationCategory.TALENT)
						.status(DonationStatus.DONATING)
						.title("TITLE_WISH_3")
						.build());
		noti1 = notificationRepository.save(
			Notification.builder()
						.userId(member.getId())
						.userType(UserType.MEMBER)
						.notifiedContentType(NotificationContentType.WISH)
						.notifiedContentId(donationWish1.getId())
						.previewMessage(donationWish1.getTitle())
						.build());
		noti2 = notificationRepository.save(
			Notification.builder()
						.userId(member.getId())
						.userType(UserType.MEMBER)
						.notifiedContentType(NotificationContentType.WISH)
						.notifiedContentId(donationWish2.getId())
						.previewMessage(donationWish2.getTitle())
						.build());
	}

	@Test
	@DisplayName("Create and send notifications")
	void createAndSendNotification() {
		notificationService.createAndSendNotification(
			member.getNickname(),
			member.getId(),
			member.getUserRole(),
			NotificationContentType.WISH,
			donationWish3.getId(),
			donationWish3.getTitle()
		);

		assertTrue(
			notificationService.getUncheckedNotifications(member.getId(), member.getUserRole())
							   .stream()
							   .anyMatch(notification -> notification.getResourceId()
																	 .equals(
																		 donationWish3.getId())));
	}

	@Test
	@DisplayName("Read unchecked notifications")
	void readUncheckedNoti() {
		List<NotificationResponse> uncheckedNotifications = notificationService
			.getUncheckedNotifications(member.getId(), UserType.MEMBER);

		assertEquals(2, uncheckedNotifications.size());
		assertTrue(uncheckedNotifications
					   .stream()
					   .anyMatch(
						   notificationResponse ->
							   notificationResponse.getResourceId()
												   .equals(
													   donationWish1.getId())));
		assertTrue(uncheckedNotifications
					   .stream()
					   .anyMatch(
						   notificationResponse ->
							   notificationResponse.getResourceId()
												   .equals(
													   donationWish2.getId())));
	}

	@Test
	@DisplayName("Read without checked notifications")
	void readWithoutCheckedNotifications() {
		notificationService.checkNotification(noti2.getId());
		List<NotificationResponse> uncheckedNotifications = notificationService
			.getUncheckedNotifications(member.getId(), UserType.MEMBER);
		assertEquals(1, uncheckedNotifications.size());
		assertTrue(uncheckedNotifications.stream()
										 .anyMatch(noti ->
													   noti.getId()
														   .equals(noti1.getId())
										 ));
	}

}
