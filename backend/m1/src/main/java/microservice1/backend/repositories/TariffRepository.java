package microservice1.backend.repositories;

import microservice1.backend.entities.TariffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<TariffEntity, Long> {

    @Query("SELECT t FROM TariffEntity t WHERE t.total_duration >= :minutos ORDER BY t.total_duration ASC")
    List<TariffEntity> findFirstByTotalDurationGreaterThanEqualOrderByTotalDurationAsc(@Param("minutos") long minutos);

    @Query("SELECT t FROM TariffEntity t ORDER BY t.total_duration DESC")
    List<TariffEntity> findTopByOrderByTotalDurationDesc();
}
