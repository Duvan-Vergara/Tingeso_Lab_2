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

    // Buscar reservas por día exacto (sin función)
    @Query("SELECT r FROM ReserveEntity r WHERE DAY(r.reserveday) = :day")
    List<ReserveEntity> findByReserveDay(@Param("date") int day);

    // Buscar reservas por mes usando JPQL
    @Query("SELECT r FROM ReserveEntity r WHERE MONTH(r.reserveday) = :month")
    List<ReserveEntity> findByReserveday_Month(@Param("month") int month);

    // Buscar reservas por mes usando rango de fechas
    @Query("SELECT r FROM ReserveEntity r WHERE r.reserveday >= :start AND r.reserveday < :end")
    List<ReserveEntity> findByReserveMonth(@Param("start") LocalDate start, @Param("end") LocalDate end);

    // Buscar reservas por mes y rut
    @Query("SELECT r FROM ReserveEntity r JOIN FETCH r.reserves_users g WHERE MONTH(r.reserveday) = :month AND g.rut = :rut")
    List<ReserveEntity> getReservesByDateMonthAndRut(@Param("rut") String rut, @Param("month") int month);

    // Buscar reservas entre fechas (ya estaba bien)
    @Query("SELECT r FROM ReserveEntity r WHERE r.reserveday BETWEEN :startDate AND :endDate")
    List<ReserveEntity> getReserveByDate_DateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Buscar reservas por mes y rut usando rango de fechas y join fetch para evitar N+1
    @Query("SELECT r FROM ReserveEntity r JOIN FETCH r.reserves_users g WHERE r.reserveday >= :start AND r.reserveday < :end AND g.rut = :rut")
    List<ReserveEntity> findByUserAndMonth(
            @Param("rut") String rut,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    // Ejemplo de paginación para grandes volúmenes
    @Query("SELECT r FROM ReserveEntity r WHERE r.reserveday BETWEEN :startDate AND :endDate")
    List<ReserveEntity> findByDateRangePaged(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);
}
