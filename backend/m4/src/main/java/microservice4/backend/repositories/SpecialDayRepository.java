package microservice4.backend.repositories;


import microservice4.backend.entities.SpecialDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDayEntity, Long> {
}
