package com.web.billim.point.domain.service;

import com.web.billim.member.domain.Member;
import com.web.billim.point.domain.SavedPoint;
import com.web.billim.point.repository.SavedPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointDomainService {

	private final SavedPointRepository savedPointRepository;

	public Map<SavedPoint, Long> use(Member member, long amount) {
//		List<SavedPoint> savedPoints = savedPointRepository.findAllNotExpired(member.getEmail());
        List<SavedPoint> savedPoints = savedPointRepository.findAllNotExpired(member.getMemberId());
		if (savedPoints.stream().mapToLong(SavedPoint::getAvailableAmount).sum() < amount) {
			throw new RuntimeException("사용가능 적립금이 부족합니다.");
		}

		Map<SavedPoint, Long> usedPointMap = new HashMap<>();
		long totalUsedAmount = 0;
		for (SavedPoint savedPoint: savedPoints) {
			long usedPoint = savedPoint.use(amount - totalUsedAmount);

			usedPointMap.put(savedPoint, usedPoint);
			totalUsedAmount += usedPoint;
			if (totalUsedAmount >= amount) break;
		}
		return usedPointMap;
	}

}

