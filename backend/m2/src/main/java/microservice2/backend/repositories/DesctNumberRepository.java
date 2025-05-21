package microservice2.backend.repositories;

import microservice2.backend.entities.DesctNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesctNumberRepository extends JpaRepository<DesctNumberEntity, Long> {

    DesctNumberEntity findByLaps(int laps);

    DesctNumberEntity findByMaxMinutes(int maxMinutes);
}
