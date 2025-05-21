package microservice2.backend.services;

import lombok.RequiredArgsConstructor;
import microservice2.backend.entities.DesctNumberEntity;
import microservice2.backend.repositories.DesctNumberRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DesctNumberService {

    private final DesctNumberRepository desctNumberRepository;

    public List<DesctNumberEntity> getTariffs(){
        return new ArrayList<>(desctNumberRepository.findAll());
    }

    public DesctNumberEntity saveTariff(DesctNumberEntity tariff) {
        calculateAdjustedPrices(tariff);
        return desctNumberRepository.save(tariff);
    }

    private void calculateAdjustedPrices(DesctNumberEntity tariff) {
        // Calcular precio de fin de semana
        tariff.setWeekendPrice(tariff.getRegularPrice() * (1 - tariff.getWeekendDiscountPercentage() / 100));
        // Calcular precio de día especial
        tariff.setHolidayPrice(tariff.getRegularPrice() * (1 + tariff.getHolidayIncreasePercentage() / 100));
    }

    public DesctNumberEntity getTariffById(Long id){
        return desctNumberRepository.findById(id).get();
    }

    public void deleteTariff(Long id){
        desctNumberRepository.deleteById(id);
    }

    public DesctNumberEntity getTariffByLaps(int laps){
        return desctNumberRepository.findByLaps(laps);
    }

    public DesctNumberEntity getTariffByMaxMinutes(int maxMinutes){
        return desctNumberRepository.findByMaxMinutes(maxMinutes);
    }

    public DesctNumberEntity getTariffByIdOrLapsOrMaxMinutes(Long id, Integer laps, Integer maxMinutes) {
        if (id != null && id > 0) {
            return getTariffById(id);
        } else if ( laps != null && laps > 0) {
            return getTariffByLaps(laps);
        } else if (maxMinutes !=null && maxMinutes > 0) {
            return getTariffByMaxMinutes(maxMinutes);
        }
        return null;
    }

}
