package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.NotificationMaster;
import com.bezkoder.springjwt.models.ReportNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportNotificationRepository extends JpaRepository<ReportNotification , Long> {
    @Query("SELECT rn From ReportNotification rn JOIN rn.students s  where rn.toRole= :role AND s= :studentId")
    List<ReportNotification> findByStudentIdAndRole(@Param("studentId") Long studentId, @Param("role") String role);

//    @Query("SELECT rn"+
//             "FROM ReportNotification rn"+
//            "WHERE rn.teacherId = :teacherId"+
//              "AND rn.toRole    = :roleName"+
//            "ORDER BY rn.date DESC")
//    List<ReportNotification> findByTeacherIdAndRole(@Param("teacherId") Long teacherId,
//                                                    @Param("roleName")  String roleName);
     List<ReportNotification> findByTeacherIdAndToRoleOrderByDateDesc(Long teacherId, String toRole);

//    @Query("SELECT rn From ReportNotification rn JOIN rn.students s  where rn.toRole= :role AND s= :studentId")
//    List<ReportNotification> findByTeacherIdAndRole(@Param("studentId") Long studentId, @Param("role") String role);

}
