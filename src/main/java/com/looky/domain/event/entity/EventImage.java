package com.looky.domain.event.entity;

import com.looky.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_image")
public class EventImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Builder
    public EventImage(String imageUrl, Integer orderIndex) {
        this.imageUrl = imageUrl;
        this.orderIndex = orderIndex;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
