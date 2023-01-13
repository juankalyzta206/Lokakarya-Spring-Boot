package com.ogya.lokakarya.telepon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ogya.lokakarya.telepon.entity.HistoryTelkom;

public interface HistoryTelkomRepository extends JpaRepository<HistoryTelkom, Long> {
	
	@Query(value="SELECT * FROM HISTORY_TELKOM ORDER BY ID_HISTORY DESC", nativeQuery = true)
	List<HistoryTelkom> findLastHistoryTelkom();

}
