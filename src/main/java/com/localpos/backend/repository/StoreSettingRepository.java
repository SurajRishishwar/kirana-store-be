package com.localpos.backend.repository;

import com.localpos.backend.entity.StoreSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreSettingRepository extends JpaRepository<StoreSetting, UUID> {

    @Query("SELECT s FROM StoreSetting s ORDER BY s.createdAt DESC LIMIT 1")
    Optional<StoreSetting> findLatest();
}
