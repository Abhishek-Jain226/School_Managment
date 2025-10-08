package com.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.Student;
import com.app.entity.StudentAttendance;

@Repository
public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Integer> {

    // Find attendance by student and date
    Optional<StudentAttendance> findByStudentAndAttendanceDate(Student student, LocalDate date);

    // Find attendance records for a student within date range
    List<StudentAttendance> findByStudentAndAttendanceDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    // Find all attendance records for a student
    List<StudentAttendance> findByStudentOrderByAttendanceDateDesc(Student student);

    // Count present days for a student in a month
    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student AND sa.attendanceDate BETWEEN :startDate AND :endDate AND sa.isPresent = true")
    Long countPresentDaysByStudentAndDateRange(@Param("student") Student student, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count absent days for a student in a month
    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student AND sa.attendanceDate BETWEEN :startDate AND :endDate AND sa.isAbsent = true")
    Long countAbsentDaysByStudentAndDateRange(@Param("student") Student student, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Count late days for a student in a month
    @Query("SELECT COUNT(sa) FROM StudentAttendance sa WHERE sa.student = :student AND sa.attendanceDate BETWEEN :startDate AND :endDate AND sa.isLate = true")
    Long countLateDaysByStudentAndDateRange(@Param("student") Student student, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Find all attendance records for a student
    List<StudentAttendance> findByStudent(Student student);

}
