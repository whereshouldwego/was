package com.example.whereshouldwego.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="places")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Place {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name="kakao_url")
    private String kakaoUrl;
    private Double x;
    private Double y;
    private String address;
    @Column(name="road_address")
    private String roadAddress;
    private String phone;
    @Column(name="ai_summary", columnDefinition = "jsonb")
    private String aiSummary;
    @Column(name="category_code")
    private String categoryCode;
    @Column(name="category_name")
    private String categoryName;

}
