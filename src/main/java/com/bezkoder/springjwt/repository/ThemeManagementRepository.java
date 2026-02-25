package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ThemeManagement;
import com.bezkoder.springjwt.payload.response.ThemeManagementResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeManagementRepository extends JpaRepository<ThemeManagement, Integer> {
    @Query("select tm from ThemeManagement as tm where tm.status='Active'")
    List<ThemeManagement> getAllActive();
    @Query("select new com.bezkoder.springjwt.payload.response.ThemeManagementResponse(tm.themeId,tm.themeName,tm.colorCode,tm.user.id,tm.user.firstName,tm.user.lastName,tm.date,tm.status) from ThemeManagement as tm where tm.user.id=:id")
    List<ThemeManagementResponse> userIdWiseTheme(@Param("id") Long id);
}
