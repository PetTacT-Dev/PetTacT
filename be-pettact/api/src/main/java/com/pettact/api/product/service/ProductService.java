package com.pettact.api.product.service;

import java.nio.file.AccessDeniedException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pettact.api.common.ViewCountService;
import com.pettact.api.common.scheduler.ViewCountScheduler.ViewCountSyncable;
import com.pettact.api.multiFile.dto.FileCreateDto;
import com.pettact.api.multiFile.dto.FileResponseDto;
import com.pettact.api.multiFile.entity.MultiFile;
import com.pettact.api.multiFile.repository.FileRepository;
import com.pettact.api.multiFile.service.FileService;
import com.pettact.api.product.dto.ProductCategoryDTO;
import com.pettact.api.product.dto.ProductCreateDTO;
import com.pettact.api.product.dto.ProductDTO;
import com.pettact.api.product.dto.ProductDetailDTO;
import com.pettact.api.product.dto.ProductUpdateDTO;
import com.pettact.api.product.entity.ProductCategoryEntity;
import com.pettact.api.product.entity.ProductEntity;
import com.pettact.api.product.repository.ProductCategoryRepository;
import com.pettact.api.product.repository.ProductRepository;
import com.pettact.api.security.vo.CustomUserDetails;
import com.pettact.api.user.entity.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ViewCountSyncable<Long> {

	private final ProductRepository productRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final FileService fileService;
	//private final ObjectMapper objectMapper;
	private final FileRepository fileRepository;
    private final ModelMapper mapper;
    private final ViewCountService viewCountService;
    private final StringRedisTemplate redisTemplate;
	
    // 카테고리 목록 
	public List<ProductCategoryDTO> getCategoryList() {
        return productCategoryRepository.findAll().stream()
            .map(e -> mapper.map(e, ProductCategoryDTO.class))
            .toList();
    }

	// 상품 상세보기
	public ProductDetailDTO getProductDetail(Long productNo, String sessionId) {

		ProductEntity product = productRepository.findById(productNo)
				.orElseThrow(() -> new RuntimeException("해당 상품을 찾을 수 없습니다."));
		
		if (product.isDeleted()) {
			throw new RuntimeException("삭제된 상품입니다.");
		}
		
		String preventKey = "product:viewed:" + sessionId + ":" + productNo;
		
		if (Boolean.FALSE.equals(redisTemplate.hasKey(preventKey))) {
		    viewCountService.increaseViewCount("product", productNo, 120);

		    String today = LocalDate.now().toString(); // "2025-07-15"
		    Long categoryId = product.getProductCategory().getCategoryNo();

		    // 전체 인기 ZSet
		    String globalKey = "product:popular:" + today;
		    redisTemplate.opsForZSet().incrementScore(globalKey, productNo.toString(), 1);
		    redisTemplate.expire(globalKey, Duration.ofDays(8));

		    // 카테고리별 인기 ZSet
		    String categoryKey = "product:popular:" + categoryId + ":" + today;
		    redisTemplate.opsForZSet().incrementScore(categoryKey, productNo.toString(), 1);
		    redisTemplate.expire(categoryKey, Duration.ofDays(8));

		    redisTemplate.opsForValue().set(preventKey, "1", Duration.ofMinutes(60));
		}

		List<MultiFile> files = fileService.getFilesByReference(MultiFile.ReferenceTable.PRODUCT, productNo);

		// 2. 이미지 URL 리스트로 변환
		List<String> imageUrls = files.stream()
				.map(file -> file.getImageUrl() != null ? "/files/" + file.getImageUrl() : "/files/" + file.getFileNo()) // 대체경로 (백엔드에서 생성한 파일 경로)
				.toList();

		return ProductDetailDTO.builder()
				.productNo(product.getProductNo())
				.productName(product.getProductName())
				.productDescription(product.getProductDescription())
				.productPrice(product.getProductPrice())
				.productStock(product.getProductStock())
				.categoryName(product.getProductCategory().getCategoryName())
				.categoryNo(product.getProductCategory().getCategoryNo())
				.createdAt(product.getCreatedAt())
				.status(product.isProductStatus())
				.userNo(product.getUser().getUserNo())
				.userName(product.getUser().getUserName()).files(files)
				.imageUrls(imageUrls) // ✅ 여러 이미지 URL 리스트
				.build();
	}

	// 상품 목록
	public Page<ProductDTO> getAllProduct(String keyword,Long categoryNo,String sort, int page, int size) {
	    //List<ProductEntity> productList;
	    
	    // 정렬 기준 설정 (createdAt 기본값)
	    Sort sortOption = Sort.by(Sort.Direction.DESC, "createdAt");

	    if ("priceAsc".equals(sort)) {
	        sortOption = Sort.by(Sort.Direction.ASC, "productPrice");
	    } else if ("priceDesc".equals(sort)) {
	        sortOption = Sort.by(Sort.Direction.DESC, "productPrice");
	    }
	    
	    // 2. Pageable 객체 생성 (페이지 번호는 0부터 시작)
	    Pageable pageable = PageRequest.of(page, size, sortOption);
	    
	    // 3. 조건에 따라 페이징된 결과 호출
	    Page<ProductEntity> productPage;
	    
	    if (keyword != null && !keyword.trim().isEmpty() && categoryNo != null) {
	        productPage = productRepository.findByProductNameContainingIgnoreCaseAndProductCategory_CategoryNoAndIsDeletedFalse(
	            keyword.trim(), categoryNo, pageable);
	    } else if (keyword != null && !keyword.trim().isEmpty()) {
	        productPage = productRepository.findByProductNameContainingIgnoreCaseAndIsDeletedFalse(keyword.trim(), pageable);
	    } else if (categoryNo != null) {
	        productPage = productRepository.findByProductCategory_CategoryNoAndIsDeletedFalse(categoryNo, pageable);
	    } else {
	        productPage = productRepository.findByIsDeletedFalse(pageable);
	    }

	 // 4. ProductEntity -> ProductDTO 매핑 후 Page<ProductDTO> 반환
	    return productPage.map(product -> ProductDTO.builder()
	            .productNo(product.getProductNo())
	            .productName(product.getProductName())
	            .productDescription(product.getProductDescription())
	            .productPrice(product.getProductPrice())
	            .productStock(product.getProductStock())
	            .createdAt(product.getCreatedAt())
	            .categoryNo(product.getProductCategory().getCategoryNo())
	            .categoryName(product.getProductCategory().getCategoryName())
	            .status(product.isProductStatus())
	            .imageUrl(product.getImageUrl() != null && !product.getImageUrl().startsWith("/files/")
	                    ? "/files/" + product.getImageUrl()
	                    : product.getImageUrl())
	            .build());
	}

	// 상품 삭제
	public void deleteProduct(Long productNo, Users user) throws AccessDeniedException {

		ProductEntity product = productRepository.findByProductNo(productNo)
				.orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
		log.info("userNo = {} , userNo = {}", product.getUser().getUserNo(), user.getUserNo());
		if (!product.getUser().getUserNo().equals(user.getUserNo())) {
			throw new AccessDeniedException("상품을 삭제할 권한이 없습니다.");
		}

		product.softDelete(); // 논리 삭제 및 삭제 시간 등록
		productRepository.save(product);
	}

	// 상품 수정
	public void updateProduct(Long id, ProductUpdateDTO dto, List<MultipartFile> files, CustomUserDetails user) {
		ProductEntity product = productRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));
		ProductCategoryEntity category = productCategoryRepository.findById(dto.getCategoryNo())
				.orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

		// 기존 상품 정보 수정
		product.setProductName(dto.getProductName());
		product.setProductDescription(dto.getProductDescription());
		product.setProductPrice(dto.getProductPrice());
		product.setProductStock(dto.getProductStock());
		product.setProductCategory(category);
		product.prePersist();
		product.setProductStatus(dto.isProductStatus());

		// ✅ 이미지 처리 시작
		List<MultiFile> existingFiles = fileRepository.findByReferenceTableAndReferenceNoOrderByFileOrderNo(
				MultiFile.ReferenceTable.PRODUCT, product.getProductNo());

		Set<String> newImageUrls = new HashSet<>(dto.getImageUrls() != null ? dto.getImageUrls() : List.of());
		for (MultiFile file : existingFiles) {
			String fileUrl = "/files/" + file.getStoredFileName();
			// String fileUrl = "/files/" + file.getStoredFileName();

			if (!newImageUrls.contains(fileUrl)) {
				// 삭제할 이미지
				//System.out.println("if탐");
				fileService.delete(file.getFileNo(), user.getUserEntity().getUserNo());
			}
		}

		// ✅ 새로 업로드된 이미지의 URL만 별도 저장
		List<String> newlyUploadedImageUrls = new ArrayList<>();

		if (files != null && !files.isEmpty()) {

		    if (dto.getImageUrls() == null) {
		        dto.setImageUrls(new ArrayList<>());
		    }

		    for (MultipartFile uploadFile : files) {
		        FileCreateDto createDto = new FileCreateDto();
		        createDto.setReferenceTable(MultiFile.ReferenceTable.PRODUCT);
		        createDto.setReferenceNo(product.getProductNo());

		        try {
		            FileResponseDto savedFileDto = fileService.createFile(createDto, uploadFile, user.getUser().getUserNo());
		            String url = "/files/" + savedFileDto.getStoredFileName();

		            dto.getImageUrls().add(url); // 전체 이미지 리스트에 추가
		            newlyUploadedImageUrls.add(url); // 새로 추가된 이미지만 따로 저장

		            System.out.println("✅ 새 이미지 업로드 성공: " + url);
		        } catch (Exception e) {
		            System.out.println("❌ 파일 업로드 실패: " + uploadFile.getOriginalFilename());
		            e.printStackTrace();
		        }
		    }
		}
		// ✅ 대표 이미지 결정: 새 이미지가 있으면 그것 중 첫 번째로 설정
		String representativeImageUrl = null;

		if (!newlyUploadedImageUrls.isEmpty()) {
		    representativeImageUrl = newlyUploadedImageUrls.get(0); // 새 이미지 중 첫 번째
		    System.out.println("✅ 대표 이미지(새 이미지): " + representativeImageUrl);
		} else if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
		    representativeImageUrl = dto.getImageUrls().get(0); // 기존 이미지 중 첫 번째
		    System.out.println("✅ 대표 이미지(기존): " + representativeImageUrl);
		}

		product.setImageUrl(representativeImageUrl);
		productRepository.save(product);
	}


	// 상품 등록
	public Long createProduct(ProductCreateDTO dto, List<MultipartFile> files, CustomUserDetails user) {
		// 1. 카테고리 조회
		ProductCategoryEntity category = productCategoryRepository.findById(dto.getCategoryNo())
				.orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

		// 2. 상품 엔티티 생성
		ProductEntity product = ProductEntity.builder()
				.productName(dto.getProductName())
				.productDescription(dto.getProductDescription())
				.productPrice(dto.getProductPrice())
				.productStock(dto.getProductStock())
				.productCategory(category)
				.productStatus(dto.isProductStatus())
				.user(user.getUserEntity())
				.build();

		// 3. 상품 저장
		ProductEntity savedProduct = productRepository.save(product);
		System.out.println(">>> 저장된 상품 번호(productNo): " + savedProduct.getProductNo());

		// 4. 파일 저장
		if (files == null) {
			System.out.println("files is null");
		} else {
			System.out.println("files is not null, size = " + files.size());
		}

		if (files != null && !files.isEmpty()) {
			for (int i = 0; i < files.size(); i++) {
				MultipartFile uploadFile = files.get(i);

				if (uploadFile == null) {
					System.out.println("files[" + i + "] is null");
					continue;
				}

				System.out.println("files[" + i + "] original filename: " + uploadFile.getOriginalFilename());
				System.out.println("files[" + i + "] size: " + uploadFile.getSize());
				System.out.println("files[" + i + "] content type: " + uploadFile.getContentType());

				FileCreateDto createDto = new FileCreateDto();
				createDto.setReferenceTable(MultiFile.ReferenceTable.PRODUCT);
				createDto.setReferenceNo(savedProduct.getProductNo());

				try {
					// ✅ 파일 저장하면서 저장된 파일 엔티티 받기
					FileResponseDto savedFileDto = fileService.createFile(createDto, uploadFile,
							user.getUser().getUserNo());
					System.out.println("파일 서비스 호출 후 savedFileDto: " + savedFileDto);

					if (i == 0) {
						System.out.println("상품 imageUrl 세팅 전: " + savedProduct.getImageUrl());

						savedProduct.setImageUrl("/files/" + savedFileDto.getStoredFileName());

						ProductEntity updatedProduct = productRepository.save(savedProduct);
						System.out.println("저장 후 상품 imageUrl: " + updatedProduct.getImageUrl());
					}
				} catch (Exception e) {
					System.out.println("파일 저장 중 예외 발생 at index " + i);
					e.printStackTrace();
				}
			}

			// ✅ imageUrl 변경 사항 반영하여 한 번 더 저장
			System.out.println(">>> imageUrl 최종 세팅: " + savedProduct.getImageUrl());
			productRepository.save(savedProduct);
			System.out.println(">>> 최종 저장 완료");
		} else {
			System.out.println("files is empty or null, skipping file save loop");
		}

		return savedProduct.getProductNo(); // 등록된 상품 번호 반환
	}
	
    // ------------------ 상품 조회수 db 갱신 ------------------
    
    @Override
    @Transactional
    public void updateViewCount(Long productNo, int count) {
    	productRepository.updateViewCount(productNo, count);
    }
    
    // ------------------ 인기 상품 TOP 10 ------------------    
	public List<ProductDTO> getPopularProducts(Long categoryNo, int count) {
	    // 1. 최근 7일 날짜별 키 구성
	    List<String> dateKeys = IntStream.rangeClosed(0, 6)
	        .mapToObj(i -> {
	            String date = LocalDate.now().minusDays(i).toString();
	            return (categoryNo == null)
	                ? "product:popular:" + date
	                : "product:popular:" + categoryNo + ":" + date;
	        })
	        .toList();
	
	    // 2. 임시 키로 합산
	    String tempKey = "product:popular:temp:" + UUID.randomUUID();
	    redisTemplate.opsForZSet().unionAndStore(dateKeys.get(0), dateKeys.subList(1, dateKeys.size()), tempKey);
	
	    // 3. 인기 상품 조회
	    Set<String> productNos = redisTemplate.opsForZSet()
	        .reverseRange(tempKey, 0, count - 1);
	
	    redisTemplate.delete(tempKey);
	
	    if (productNos == null || productNos.isEmpty()) return List.of();
	
	    // 4. DB 조회 + 순서 보정
	    List<Long> ids = productNos.stream().map(Long::parseLong).toList();
	    List<ProductEntity> products = productRepository.findAllById(ids);
	    Map<Long, ProductEntity> map = products.stream()
	        .collect(Collectors.toMap(ProductEntity::getProductNo, Function.identity()));
	
	    // 5. DTO 변환
	    return ids.stream()
	        .map(map::get)
	        .filter(Objects::nonNull)
	        .map(product -> mapper.map(product, ProductDTO.class)) // or ProductDTO.fromEntity(product)
	        .toList();
	}

    // 
    public Page<ProductDTO> getMyProducts(Long userNo, String keyword, Long categoryNo, String sort, int page, int size) {
        Sort sortOption = Sort.by(Sort.Direction.DESC, "createdAt");

        if ("priceAsc".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.ASC, "productPrice");
        } else if ("priceDesc".equals(sort)) {
            sortOption = Sort.by(Sort.Direction.DESC, "productPrice");
        }

        Pageable pageable = PageRequest.of(page, size, sortOption);

        Page<ProductEntity> productPage;

        if (keyword != null && !keyword.trim().isEmpty() && categoryNo != null) {
            productPage = productRepository
                .findByUser_UserNoAndProductNameContainingIgnoreCaseAndProductCategory_CategoryNoAndIsDeletedFalse(
                    userNo, keyword.trim(), categoryNo, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productRepository
                .findByUser_UserNoAndProductNameContainingIgnoreCaseAndIsDeletedFalse(
                    userNo, keyword.trim(), pageable);
        } else if (categoryNo != null) {
            productPage = productRepository
                .findByUser_UserNoAndProductCategory_CategoryNoAndIsDeletedFalse(userNo, categoryNo, pageable);
        } else {
            productPage = productRepository
                .findByUser_UserNoAndIsDeletedFalse(userNo, pageable);
        }

        return productPage.map(product -> ProductDTO.builder()
            .productNo(product.getProductNo())
            .productName(product.getProductName())
            .productDescription(product.getProductDescription())
            .productPrice(product.getProductPrice())
            .productStock(product.getProductStock())
            .createdAt(product.getCreatedAt())
            .categoryNo(product.getProductCategory().getCategoryNo())
            .categoryName(product.getProductCategory().getCategoryName())
            .status(product.isProductStatus())
            .imageUrl(product.getImageUrl() != null && !product.getImageUrl().startsWith("/files/")
                ? "/files/" + product.getImageUrl()
                : product.getImageUrl())
            .build());
    }

}
