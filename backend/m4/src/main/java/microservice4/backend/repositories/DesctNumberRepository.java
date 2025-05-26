package microservice4.backend.repositories;

import microservice4.backend.entities.DesctNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesctNumberRepository extends JpaRepository<DesctNumberEntity, Long> {

    DesctNumberEntity findFirstByMinpersonasLessThanEqualAndMaxpersonasGreaterThanEqual(int minpersonas, int maxpersonas);
}
