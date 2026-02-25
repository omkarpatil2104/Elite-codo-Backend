package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.ReportsToSuperAdminMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportsToSuperAdminMasterRepository extends JpaRepository<ReportsToSuperAdminMaster, Integer> {
    @Query("select r from ReportsToSuperAdminMaster as r where r.senderId=:senderId")
    List<ReportsToSuperAdminMaster> senderIdWiseReports(@Param("senderId") Long senderId);

    @Query("select r from ReportsToSuperAdminMaster as r where r.replyReportsToSuperAdminId=:reportsToSuperAdminId")
    ReportsToSuperAdminMaster getReply(@Param("reportsToSuperAdminId") Integer reportsToSuperAdminId);
}
