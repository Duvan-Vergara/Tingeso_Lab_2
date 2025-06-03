package microservice5.backend.repositories;

import microservice5.backend.entities.ReserveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<ReserveEntity, Long> {

    // Buscar reservas por mes y rut
    @Query("SELECT r FROM ReserveEntity r JOIN FETCH r.reserves_users g WHERE MONTH(r.reserveday) = :month AND g.rut = :rut")
    List<ReserveEntity> getReservesByDateMonthAndRut(@Param("rut") String rut, @Param("month") int month);

    // Buscar reservas entre fechas (ya estaba bien)
    @Query("SELECT r FROM ReserveEntity r WHERE r.reserveday BETWEEN :startDate AND :endDate")
    List<ReserveEntity> getReserveByDate_DateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
