package microservice1.backend.repositories;

import microservice1.backend.entities.TariffEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TariffRepository extends JpaRepository<TariffEntity, Long> {

    TariffEntity findByLaps(int laps);

    TariffEntity findByMaxMinutes(int maxMinutes);
}
