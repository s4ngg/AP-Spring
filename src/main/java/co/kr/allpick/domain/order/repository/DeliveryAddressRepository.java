package co.kr.allpick.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import co.kr.allpick.domain.order.entity.DeliveryAddress;

@Component("deliveryAddressRepositoryHandler")
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    List<DeliveryAddress> findByMemberIdAndDeletedAtIsNull(Long memberId);

    boolean existsByMemberIdAndAddressAndAddressDetail(Long memberId, String address, String addressDetail);
}