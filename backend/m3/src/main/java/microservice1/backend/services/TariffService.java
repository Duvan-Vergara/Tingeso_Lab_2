package microservice1.backend.services;

import lombok.RequiredArgsConstructor;
import microservice1.backend.entities.TariffEntity;
import microservice1.backend.repositories.TariffRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;

    public List<TariffEntity> getTariffs(){
        return new ArrayList<>(tariffRepository.findAll());
    }

    public TariffEntity saveTariff(TariffEntity tariff) {
        calculateAdjustedPrices(tariff);
        return tariffRepository.save(tariff);
    }

    private void calculateAdjustedPrices(TariffEntity tariff) {
        // Calcular precio de fin de semana
        tariff.setWeekendPrice(tariff.getRegularPrice() * (1 - tariff.getWeekendDiscountPercentage() / 100));
        // Calcular precio de día especial
        tariff.setHolidayPrice(tariff.getRegularPrice() * (1 + tariff.getHolidayIncreasePercentage() / 100));
    }

    public TariffEntity getTariffById(Long id){
        return tariffRepository.findById(id).get();
    }

    public void deleteTariff(Long id){
        tariffRepository.deleteById(id);
    }

    public TariffEntity getTariffByLaps(int laps){
        return tariffRepository.findByLaps(laps);
    }

    public TariffEntity getTariffByMaxMinutes(int maxMinutes){
        return tariffRepository.findByMaxMinutes(maxMinutes);
    }

    public TariffEntity getTariffByIdOrLapsOrMaxMinutes(Long id, Integer laps, Integer maxMinutes) {
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
