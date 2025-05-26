package microservice3.backend.repositories;

import microservice3.backend.entities.DesctFrecuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesctFrecuRepository extends JpaRepository<DesctFrecuEntity, Long> {
    DesctFrecuEntity findFirstByMinvecesLessThanEqualAndMaxvecesGreaterThanEqual(int veces1, int veces2);
}
