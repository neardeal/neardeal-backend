package com.neardeal.domain.store.service;

import com.neardeal.domain.store.entity.StoreCategory;

import com.neardeal.common.exception.CustomException;
import com.neardeal.common.exception.ErrorCode;
import com.neardeal.common.response.PageResponse;
import com.neardeal.domain.store.dto.CreateStoreRequest;
import com.neardeal.domain.store.dto.StoreResponse;
import com.neardeal.domain.store.dto.UpdateStoreRequest;
import com.neardeal.domain.store.entity.Store;
import com.neardeal.domain.store.repository.StoreRepository;
import com.neardeal.domain.user.entity.Role;
import com.neardeal.domain.user.repository.UserRepository;
import com.neardeal.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createStore(User user, CreateStoreRequest request) {

        User owner = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (owner.getRole() != Role.ROLE_OWNER) {
            log.warn("[CreateStore] Forbidden access. userId={}, role={}", user.getId(), owner.getRole());
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (storeRepository.existsByName(request.getName())) {
            log.warn("[CreateStore] Duplicate store name: {}", request.getName());
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 상점 이름입니다.");
        }

        Store store = request.toEntity(owner);
        Store savedStore = storeRepository.save(store);
        log.info("[CreateStore] Success. storeId={}, ownerId={}", savedStore.getId(), owner.getId());
        return savedStore.getId();
    }

    public StoreResponse getStore(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "상점을 찾을 수 없습니다."));

        return StoreResponse.from(store);
    }

    public PageResponse<StoreResponse> getStores(String keyword, StoreCategory category, Pageable pageable) {
        Page<Store> storePage;

        if (keyword != null && category != null) {
            storePage = storeRepository.findByNameContainingAndStoreCategory(keyword, category, pageable);
        } else if (keyword != null) {
            storePage = storeRepository.findByNameContaining(keyword, pageable);
        } else if (category != null) {
            storePage = storeRepository.findByStoreCategory(category, pageable);
        } else {
            storePage = storeRepository.findAll(pageable);
        }

        Page<StoreResponse> responsePage = storePage.map(StoreResponse::from);
        return PageResponse.from(responsePage);
    }

    @Transactional
    public void updateStore(Long storeId, User user, UpdateStoreRequest request) {
        User owner = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "가게를 찾을 수 없습니다."));

        // 본인 소유 확인
        if (!Objects.equals(store.getUser().getId(), owner.getId())) {
            log.warn("[UpdateStore] Forbidden attempt. storeId={}, requesterUserId={}", storeId, owner.getId());
            throw new CustomException(ErrorCode.FORBIDDEN, "본인 소유의 가게가 아닙니다.");
        }

        if (!store.getName().equals(request.getName()) && storeRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 상점 이름입니다.");
        }

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getPhoneNumber(),
                request.getIntroduction(),
                request.getOperatingHours(),
                request.getStoreCategory());
        log.info("[UpdateStore] Success. storeId={}", storeId);
    }

    @Transactional
    public void deleteStore(Long storeId, User user) {
        User owner = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND, "가게를 찾을 수 없습니다."));

        // 본인 소유 확인
        if (!Objects.equals(store.getUser().getId(), owner.getId())) {
            log.warn("[DeleteStore] Forbidden attempt. storeId={}, requesterUserId={}", storeId, owner.getId());
            throw new CustomException(ErrorCode.FORBIDDEN, "본인 소유의 가게가 아닙니다.");
        }

        storeRepository.delete(store);
        log.info("[DeleteStore] Success. storeId={}", storeId);
    }

    public List<StoreResponse> getMyStores(User user) {
        User owner = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Store> stores = storeRepository.findAllByUser(owner);
        return stores.stream().map(StoreResponse::from).toList();
    }
}
