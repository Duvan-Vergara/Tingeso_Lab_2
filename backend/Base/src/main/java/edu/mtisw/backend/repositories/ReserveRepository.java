package edu.mtisw.backend.repositories;

import edu.mtisw.backend.entities.ReserveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<ReserveEntity, Long> {

    @Query("SELECT r FROM ReserveEntity r WHERE FUNCTION('DAY', r.date) = :dateDay")
    List<ReserveEntity> getReserveByDate_Day(@Param("dateDay") int dateDay);

    @Query("SELECT r FROM ReserveEntity r WHERE FUNCTION('MONTH', r.date) = :dateMonth")
    List<ReserveEntity> getReserveByDate_Month(@Param("dateMonth") int dateMonth);

    @Query("SELECT r FROM ReserveEntity r WHERE r.date BETWEEN :startDate AND :endDate")
    List<ReserveEntity> getReserveByDate_DateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM ReserveEntity r JOIN r.group g WHERE FUNCTION('MONTH', r.date) = :month AND g.rut = :rut")
    List<ReserveEntity> getReservesByDateMonthAndRut(@Param("rut") String rut, @Param("month") int month);
}
