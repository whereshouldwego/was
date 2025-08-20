package com.example.whereshouldwego.features.place.repository;

import com.example.whereshouldwego.features.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Modifying
    @Query(value= """
    INSERT INTO PLACES (id, name, kakao_url, x,y,address,road_address, phone, category_code, category_name)
    VALUES (:id, :name, :kakaoUrl, :x, :y, :address, :road_address, :phone, :category_code, :category_name)
    ON CONFLICT (id) DO NOTHING
""", nativeQuery = true)
    void insertOrIgnore(@Param("id") Long id,
                        @Param("name") String name,
                        @Param("kakaoUrl") String kakaoUrl,
                        @Param("x") Double x,
                        @Param("y") Double y,
                        @Param("address") String address,
                        @Param("road_address") String roadAddress,
                        @Param("phone") String phone,
                        @Param("category_code") String categoryCode,
                        @Param("category_name") String categoryName
                        );

    @Query("SELECT p.id FROM Place p WHERE p.id IN :ids")
    Set<Long> findExistingIds(@Param("ids") Set<Long> ids);
}
