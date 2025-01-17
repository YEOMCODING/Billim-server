package com.web.billim.product.controller;

import com.web.billim.order.service.OrderService;
import com.web.billim.product.domain.Product;
import com.web.billim.product.domain.ProductCategory;
import com.web.billim.product.dto.ProductRegisterCommand;
import com.web.billim.product.dto.ProductUpdateCommand;
import com.web.billim.product.dto.request.InterestRequest;
import com.web.billim.product.dto.request.ProductRegisterRequest;
import com.web.billim.product.dto.request.ProductUpdateRequest;
import com.web.billim.product.dto.response.*;
import com.web.billim.product.service.ProductInterestService;
import com.web.billim.product.service.ProductService;
import com.web.billim.review.service.ReviewService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final OrderService orderService;
    private final ProductInterestService productInterestService;
    private final ReviewService reviewService;

    @ApiOperation(value = "상품 등록", notes = "이미지 1장 부터 최대 5장까지 첨부 가능")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> registerProduct(
            @AuthenticationPrincipal long memberId,
            @ModelAttribute @Valid ProductRegisterRequest req
    ) {
        req.setRegisterMember(memberId);
        ProductRegisterCommand command = new ProductRegisterCommand(req);
        return ResponseEntity.ok(productService.register(command));
    }

    @ApiOperation(value = "*상품 수정")
    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct(
            @AuthenticationPrincipal long memberId,
            @ModelAttribute@Valid ProductUpdateRequest req
    ) {
        req.setRegisterMember(memberId);
        ProductUpdateCommand command = new ProductUpdateCommand(req);
        return ResponseEntity.ok(productService.update(command));
    }


    @ApiOperation(value = "전체 상품목록 조회, 검색, 페이징", notes = "전체 상품목록 조회, 카테고리별 검색, 키워드 검색, 페이징 처리")
    @Transactional
    @GetMapping("/list/search")
    public ResponseEntity<Page<ProductListResponse>> productList(
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "1", value = "page") int page
    ) {
        PageRequest paging = PageRequest.of(page - 1, 20);
        return ResponseEntity.ok(productService.search(category, keyword, paging));
    }

    @GetMapping("/list/category")
    public ResponseEntity<List<ProductCategory>> productCategory() {
        List<ProductCategory> categoryList = productService.categoryList();
        return ResponseEntity.ok(categoryList);
    }

    @ApiOperation(value = "상품 상세정보", notes = "productId에 따른 상품 상세정보 & 이미 예약되어 이용할 수 없는 날짜")
    @GetMapping("/detail/{productId}")
    public ResponseEntity<ProductDetailResponse> productDetail(@PathVariable("productId") long productId) {
        ProductDetailResponse resp = productService.retrieveDetail(productId);
        resp.setProductReviewLists(reviewService.reviewList(productId));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/detail/date/{productId}")
    public ResponseEntity<List<LocalDate>> alreadyReservedDate(@PathVariable("productId") long productId) {
        List<LocalDate> dates = orderService.reservationDate(productId);
        return ResponseEntity.ok(dates);
    }

    @ApiOperation(value = "상품 수정 기존 내용 조회", notes = "상품 수정시 기존 정보 조회")
    @GetMapping("/update/{productId}")
    public ResponseEntity<ProductUpdateResponse> updateProductResponse(
            @PathVariable("productId") long productId
    ) {
        ProductUpdateResponse resp = productService.retrieveUpdateProduct(productId);
        return ResponseEntity.ok(resp);
    }

//    @ApiOperation(value = "*상품 수정")
//    @PutMapping("/update")
//    public ResponseEntity<Void> updateProduct(
//            @AuthenticationPrincipal long memberId,
//			@ModelAttribute@Valid ProductUpdateRequest req
//    ) {
//        productService.update(req);
//        return ResponseEntity.ok().build();
//    }

    @ApiOperation(value = "상품 삭제", notes = "해당 회원이 작성한 상품 및 상품 이미지 삭제")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @AuthenticationPrincipal long memberId,
            @PathVariable("productId") long productId
    ) {
        productService.delete(memberId, productId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "*인기 상품 조회", notes = "사람들이 많이 본 상품 사진 리스트")
    @GetMapping("/list/most/popular")
    public ResponseEntity<List<MostProductList>> mostProductList() {
        return ResponseEntity.ok(productService.findMostPopularProduct());
    }

    @ApiOperation(value = "관심상품 등록, 삭제", notes = "true 관심상품등록, false 관심등록삭제")
    @PostMapping("/interest")
    public ResponseEntity<Void> saveOrDeleteInterest(
            @AuthenticationPrincipal long memberId,
            @RequestBody InterestRequest interestRequest
    ) {
        productInterestService.saveOrDeleteInterest(memberId, interestRequest);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "마이페이지 관심목록 조회")
    @GetMapping("/my/interestList")
    public ResponseEntity<MyInterestProductList> myInterestProductList(@AuthenticationPrincipal long memberId) {
        return ResponseEntity.ok(productInterestService.myInterestProductList(memberId));
    }

    // 판매중인 상품 목록 조회
    @GetMapping("/my/sell/list")
    public ResponseEntity<Void> mySellList() {
        return ResponseEntity.ok().build();
    }

    // 판매중인 상품 클릭시 판매 주문 내역 조회
    @GetMapping("/my/sell/detail")
    public ResponseEntity<Void> mySellDetail() {
        return ResponseEntity.ok().build();
    }

}

