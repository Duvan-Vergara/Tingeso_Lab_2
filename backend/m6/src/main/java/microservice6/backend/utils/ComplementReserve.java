package microservice5.backend.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ComplementReserve {

    public int calculateBirthdayLimit(int numberOfPeople) {
        if (numberOfPeople >= 3 && numberOfPeople <= 5) {
            return 1;
        } else if (numberOfPeople >= 6 && numberOfPeople <= 10) {
            return 2;
        }
        return 0;
    }

    public boolean isBirthday(UserEntity user, LocalDate date) {
        if(user.getBirthDate() == null) {
            return false;
        }
        return user.getBirthDate().getMonth() == date.getMonth() && user.getBirthDate().getDayOfMonth() == date.getDayOfMonth();
    }

}
