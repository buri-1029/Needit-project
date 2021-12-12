package com.prgrms.needit.domain.center.repository;

import com.prgrms.needit.domain.center.entity.Center;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CenterRepository extends JpaRepository<Center, Long> {

	Optional<Center> findByIdAndIsDeletedFalse(Long centerId);

	boolean existsByEmail(String email);

	boolean existsByName(String name);
  
	Optional<Center> findByEmailAndIsDeletedFalse(String email);
}
