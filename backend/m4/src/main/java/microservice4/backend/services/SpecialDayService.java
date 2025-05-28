package microservice4.backend.services;


import microservice4.backend.entities.SpecialDayEntity;
import microservice4.backend.repositories.SpecialDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialDayService {

    @Autowired
    SpecialDayRepository specialDayRepository;

    public List<SpecialDayEntity> getSpecialDays() {
        return specialDayRepository.findAll();
    }

    public SpecialDayEntity saveSpecialDay(SpecialDayEntity specialDay) {
        return specialDayRepository.save(specialDay);
    }

    public SpecialDayEntity getSpecialDayById(Long id) {
        return specialDayRepository.findById(id).get();
    }

    public boolean deleteSpecialDayByID(Long id) throws Exception {
        try {
            specialDayRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
