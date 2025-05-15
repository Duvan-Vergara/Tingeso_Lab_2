package edu.mtisw.backend.repositories;

import edu.mtisw.backend.entities.SpecialDayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialDayRepository extends JpaRepository<SpecialDayEntity, Long> {
}
