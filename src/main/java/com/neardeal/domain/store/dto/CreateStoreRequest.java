package com.neardeal.domain.store.dto;

import com.neardeal.domain.store.entity.Store;
import com.neardeal.domain.store.entity.StoreCategory;
import com.neardeal.domain.store.entity.StoreMood;
import com.neardeal.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateStoreRequest {

    @NotBlank(message = "가게 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private Double latitude;
    private Double longitude;

    private String phoneNumber;

    private String introduction;

    private String operatingHours;

    @NotNull(message = "카테고리는 필수입니다.")
    private List<StoreCategory> storeCategories;

    private List<StoreMood> storeMoods;

    public Store toEntity(User user) {
        return Store.builder()
                .user(user)
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .phoneNumber(phoneNumber)
                .introduction(introduction)
                .operatingHours(operatingHours)
                .storeCategories(new HashSet<>(storeCategories))
                .storeMoods(storeMoods != null ? new HashSet<>(storeMoods) : new HashSet<>())
                .build();
    }
}
