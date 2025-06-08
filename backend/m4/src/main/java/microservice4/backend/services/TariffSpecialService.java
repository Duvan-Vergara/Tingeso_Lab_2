package microservice4.backend.services;

import lombok.AllArgsConstructor;
import microservice4.backend.dto.TarifaDTO;
import microservice4.backend.entities.TariffSpecial;
import microservice4.backend.repositories.SpecialDayRepository;
import microservice4.backend.repositories.TariffSpecialRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class TariffSpecialService {
    private final TariffSpecialRepository tariffSpecialRepository;

    private final SpecialDayRepository specialRepository;

    public TariffSpecial getTariffSpecial() {
        TariffSpecial tariff = tariffSpecialRepository.findFirstByOrderByIdAsc();
        if (tariff == null) {
            throw new IllegalStateException("No se ha configurado una tarifa especial.");
        }
        return tariff;
    }

    public void saveTariffSpecial(TarifaDTO tarifaDTO) {
        if (tarifaDTO == null
                || tarifaDTO.getWeekendDiscountPercentage() < 0
                || tarifaDTO.getHolidayIncreasePercentage() < 0) {
            throw new IllegalArgumentException("TarifaDTO no puede tener valores nulos o negativos");
        }

        if(tariffSpecialRepository.count() > 0) {
            return;
        }

        double weekendDiscount = tarifaDTO.getWeekendDiscountPercentage();
        double holidayIncrease = tarifaDTO.getHolidayIncreasePercentage();

        TariffSpecial tariffSpecial = new TariffSpecial();
        tariffSpecial.setWeekendDiscountPercentage(weekendDiscount);
        tariffSpecial.setHolidayIncreasePercentage(holidayIncrease);

        tariffSpecialRepository.save(tariffSpecial);
    }

    public void updateTariffSpecial(TarifaDTO tarifaDTO) {
        if (tariffSpecialRepository.count() == 0) {
            throw new IllegalStateException("No se ha configurado una tarifa especial.");
        }

        TariffSpecial tariffSpecial = tariffSpecialRepository.findFirstByOrderByIdAsc();

        if (tariffSpecial == null) {
            throw new IllegalStateException("No se ha encontrado la tarifa especial.");
        }

        if (tarifaDTO.getWeekendDiscountPercentage() < 0 || tarifaDTO.getHolidayIncreasePercentage() < 0) {
            throw new IllegalArgumentException("Los valores de descuento no pueden ser negativos.");
        }

        tariffSpecial.setWeekendDiscountPercentage(tarifaDTO.getWeekendDiscountPercentage());
        tariffSpecial.setHolidayIncreasePercentage(tarifaDTO.getHolidayIncreasePercentage());

        tariffSpecialRepository.save(tariffSpecial);
    }

    public double getPrice(LocalDate date, double price) {
        TariffSpecial tariff  = tariffSpecialRepository.findFirstByOrderByIdAsc();
        if (tariff == null) {
            throw new IllegalStateException("No se ha configurado una tarifa especial.");
        }
        if (date == null || price < 0) {
            throw new IllegalArgumentException("Fecha no puede ser nula y el precio no puede ser negativo.");
        }
        boolean isSpecialDay = specialRepository.findAll()
                .stream()
                .anyMatch(specialDay -> specialDay.getDate().equals(date));
        if (isSpecialDay) {
            return price * (1 + tariff.getHolidayIncreasePercentage() / 100);
        } else if (date.getDayOfWeek().getValue() >= 6) {
            return price * (1 - tariff.getWeekendDiscountPercentage() / 100);
        } else {
            return price;
        }
    }
}
