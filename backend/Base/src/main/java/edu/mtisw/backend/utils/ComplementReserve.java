package edu.mtisw.backend.utils;

import edu.mtisw.backend.entities.ReserveEntity;
import edu.mtisw.backend.entities.UserEntity;
import edu.mtisw.backend.repositories.SpecialDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ComplementReserve {

    @Autowired
    private SpecialDayRepository specialDayRepository;

    public int calculateBirthdayLimit(int numberOfPeople) {
        if (numberOfPeople >= 3 && numberOfPeople <= 5) {
            return 1;
        } else if (numberOfPeople >= 6 && numberOfPeople <= 10) {
            return 2;
        }
        return 0;
    }

    public double calculateGroupSizeDiscount(int numberOfPeople) {
        if (numberOfPeople >= 3 && numberOfPeople <= 5) {
            return 0.10;
        } else if (numberOfPeople >= 6 && numberOfPeople <= 10) {
            return 0.20;
        } else if (numberOfPeople >= 11 && numberOfPeople <= 15) {
            return 0.30;
        }
        return 0;
    }

    public double calculateFrequentCustomerDiscount(List<ReserveEntity> userReserves) {
        int visitsCount = userReserves.size();
        if (visitsCount >= 7) {
            return 0.30;
        } else if (visitsCount >= 5) {
            return 0.20;
        } else if (visitsCount >= 2) {
            return 0.10;
        }
        return 0;
    }

    public double calculateBestDiscount(ReserveEntity reserve, List<ReserveEntity> userReserves) {
        double bestDiscount = 0;
        int numberOfPeople = reserve.getGroup().size();
        // Descuento por número de personas
        bestDiscount = Math.max(bestDiscount, calculateGroupSizeDiscount(numberOfPeople));
        // Descuento para clientes frecuentes
        bestDiscount = Math.max(bestDiscount, calculateFrequentCustomerDiscount(userReserves));
        return bestDiscount;
    }

    public boolean isSpecialDay(LocalDate date) {
        return specialDayRepository.findAll().stream()
                .anyMatch(specialDay -> specialDay.getDate().equals(date));
    }

    public boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7;
    }

    public boolean isBirthday(UserEntity user, LocalDate date) {
        if(user.getBirthDate() == null) {
            return false;
        }
        return user.getBirthDate().getMonth() == date.getMonth() && user.getBirthDate().getDayOfMonth() == date.getDayOfMonth();
    }

}
