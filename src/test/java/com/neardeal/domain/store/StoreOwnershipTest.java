package com.neardeal.domain.store;

import com.neardeal.domain.store.dto.CreateStoreRequest;
import com.neardeal.domain.store.dto.StoreResponse;
import com.neardeal.domain.store.entity.StoreCategory;
import com.neardeal.domain.store.repository.StoreRepository;
import com.neardeal.domain.store.service.StoreService;
import com.neardeal.domain.user.entity.Gender;
import com.neardeal.domain.user.entity.Role;
import com.neardeal.domain.user.entity.User;
import com.neardeal.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StoreOwnershipTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @DisplayName("점주는 여러 개의 가게를 등록하고 조회할 수 있다")
    void createAndGetMultipleStores() {
        // given
        User owner = User.builder()
                .username("owner1")
                .password("password")
                .email("owner1@example.com")
                .name("점주1")
                .phoneNumber("010-1234-5678")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.ROLE_OWNER)
                .build();
        userRepository.save(owner);

        CreateStoreRequest request1 = CreateStoreRequest.builder()
                .name("가게1")
                .address("서울시 강남구")
                .storeCategory(StoreCategory.RESTAURANT)
                .build();

        CreateStoreRequest request2 = CreateStoreRequest.builder()
                .name("가게2")
                .address("서울시 서초구")
                .storeCategory(StoreCategory.CAFE)
                .build();

        // when
        storeService.createStore(owner, request1);
        storeService.createStore(owner, request2);

        // then
        List<StoreResponse> myStores = storeService.getMyStores(owner);
        assertThat(myStores).hasSize(2);
        assertThat(myStores).extracting("name")
                .containsExactlyInAnyOrder("가게1", "가게2");
    }
}
