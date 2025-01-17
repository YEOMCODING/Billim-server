package com.web.billim.review.service;

import com.web.billim.common.exception.NotFoundException;
import com.web.billim.common.exception.handler.ErrorCode;
import com.web.billim.order.domain.ProductOrder;
import com.web.billim.order.service.OrderService;
import com.web.billim.review.domain.Review;
import com.web.billim.review.dto.request.ReviewWriteRequest;
import com.web.billim.review.dto.response.ProductReviewList;
import com.web.billim.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderService orderService;

	public double calculateStarRating(long productId) {
		return reviewRepository.findAllByProductId(productId).stream()
			.collect(Collectors.summarizingLong(Review::getStarRating))
			.getAverage();
	}

	public void findMyProductReview(long memberId) {
	}

	public void productReviewWrite(ReviewWriteRequest reviewWriteRequest) {
		ProductOrder productOrder =  orderService.findByOrder(reviewWriteRequest.getOrderId());
		reviewRepository.save(ReviewWriteRequest.of(reviewWriteRequest,productOrder));
	}

	public long myReviewNoCount(long memberId) {
		long orders = orderService.numberOfOrders(memberId);
		long reviews = reviewRepository.countByMemberId(memberId)
				.orElseThrow(()-> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));
		return orders - reviews;
	}

	public List<ProductReviewList> reviewList(long productId) {
		return reviewRepository.findAllByProductId(productId)
				.stream()
				.map(ProductReviewList::of)
				.collect(Collectors.toList());
	}

}
