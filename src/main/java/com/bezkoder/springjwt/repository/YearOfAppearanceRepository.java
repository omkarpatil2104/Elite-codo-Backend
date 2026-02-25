package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.YearOfAppearance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YearOfAppearanceRepository extends JpaRepository<YearOfAppearance, Integer> {
    @Query("select y from YearOfAppearance as y where y.status='Active'")
    List<YearOfAppearance> getAllActive();
}
