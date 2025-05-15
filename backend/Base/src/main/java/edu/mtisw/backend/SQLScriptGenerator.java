package edu.mtisw.backend;

import edu.mtisw.backend.entities.ReserveEntity;
import edu.mtisw.backend.entities.SpecialDayEntity;
import edu.mtisw.backend.entities.TariffEntity;
import edu.mtisw.backend.entities.UserEntity;
import edu.mtisw.backend.services.ReserveService;
import edu.mtisw.backend.services.SpecialDayService;
import edu.mtisw.backend.services.TariffService;
import edu.mtisw.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
public class SQLScriptGenerator {

    @Autowired
    private TariffService tariffService;

    @Autowired
    private SpecialDayService specialDayService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReserveService reserveService;

    public void generateSQLScript(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("-- Vaciar todas las tablas\n");
            writer.write("SET FOREIGN_KEY_CHECKS = 0;\n");
            writer.write("TRUNCATE TABLE reserves_users;\n");
            writer.write("TRUNCATE TABLE reserves;\n");
            writer.write("TRUNCATE TABLE users;\n");
            writer.write("TRUNCATE TABLE special_days;\n");
            writer.write("TRUNCATE TABLE tariffs;\n");
            writer.write("SET FOREIGN_KEY_CHECKS = 1;\n\n");

            writer.write("-- Modificar columnas 'begin' y 'finish' de DATETIME a TIME\n");
            writer.write("ALTER TABLE reserves MODIFY COLUMN begin TIME NOT NULL;\n");
            writer.write("ALTER TABLE reserves MODIFY COLUMN finish TIME NOT NULL;\n\n");

            writer.write("-- Script para poblar la base de datos\n\n");
            // Generar sentencias para tarifas
            writer.write("-- Poblar tabla 'tariffs'\n");
            List<TariffEntity> tariffs = generateTariffs();
            for (TariffEntity tariff : tariffs) {
                writer.write(String.format(Locale.US,
                        "INSERT INTO tariffs (laps, max_minutes, regular_price, total_duration, weekend_price, holiday_price, holiday_increase_percentage, weekend_discount_percentage) " +
                                "VALUES (%d, %d, %.2f, %d, %.2f, %.2f, %.2f, %.2f);\n",
                        tariff.getLaps(), tariff.getMaxMinutes(), tariff.getRegularPrice(),
                        tariff.getTotalDuration(), tariff.getWeekendPrice(), tariff.getHolidayPrice(),
                        tariff.getHolidayIncreasePercentage(), tariff.getWeekendDiscountPercentage()
                ));
            }
            writer.write("\n");

            // Generar sentencias para días especiales
            writer.write("-- Poblar tabla 'special_days'\n");
            List<SpecialDayEntity> specialDays = generateSpecialDays();
            for (SpecialDayEntity specialDay : specialDays) {
                writer.write(String.format(
                        "INSERT INTO special_days (date, description) VALUES ('%tF', '%s');\n",
                        specialDay.getDate(), specialDay.getDescription()
                ));
            }
            writer.write("\n");

            // Generar sentencias para usuarios
            writer.write("-- Poblar tabla 'users'\n");
            List<UserEntity> users = generateUsers(50);
            for (UserEntity user : users) {
                writer.write(String.format(
                        "INSERT INTO users (rut, name, lastname, email, birthdate) " +
                                "VALUES ('%s', '%s', '%s', '%s', '%tF');\n",
                        user.getRut(), user.getName(), user.getLastName(),
                        "duvanvch12@gmail.com", user.getBirthDate()
                ));
            }
            writer.write("\n");

            // Generar sentencias para reservas
            writer.write("-- Poblar tabla 'reserves'\n");
            List<ReserveEntity> reserves = generateReserves(100, users, tariffs, specialDays);
            for (ReserveEntity reserve : reserves) {
                writer.write(String.format(Locale.US,
                        "INSERT INTO reserves (reserveday, begin, finish, tariff_id, final_price) " +
                                "VALUES ('%tF', '%s', '%s', %d, %.2f);\n",
                        reserve.getDate(), reserve.getBegin(), reserve.getFinish(),
                        tariffs.indexOf(reserve.getTariff()) + 1, reserve.getFinalPrice()
                ));

                // Generar sentencias para la relación ManyToMany (reserves_users)
                writer.write("-- Poblar tabla 'reserves_users'\n");
                for (UserEntity user : reserve.getGroup()) {
                    writer.write(String.format(
                            "INSERT INTO reserves_users (reserve_id, user_id) VALUES (%d, %d);\n",
                            reserve.getId(), user.getId()
                    ));
                }
            }
        }
    }

    private List<TariffEntity> generateTariffs() {
        return Arrays.asList(
                new TariffEntity(1L, 10, 10, 15000, 30, 5.0, 20.0, 14250, 18000),
                new TariffEntity(2L, 15, 15, 20000, 35, 5.0, 20.0, 19000, 24000),
                new TariffEntity(3L, 20, 20, 25000, 40, 5.0, 20.0, 23750, 30000)
        );
    }

    private List<SpecialDayEntity> generateSpecialDays() {
        List<SpecialDayEntity> specialDays = new ArrayList<>();
        long specialDayId = 1;
        specialDays.add(new SpecialDayEntity(specialDayId, LocalDate.of(2023, 1, 1), "Año Nuevo"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 4, 18), "Viernes Santo"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 4, 19), "Sábado Santo"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 5, 1), "Día del Trabajo"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 5, 21), "Día de las Glorias Navales"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 6, 20), "Día Nacional de los Pueblos Indígenas"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 6, 29), "San Pedro y San Pablo"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 7, 16), "Día de la Virgen del Carmen"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 8, 15), "Asunción de la Virgen"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 9, 18), "Independencia Nacional"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 9, 19), "Día de las Glorias del Ejército"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 10, 12), "Encuentro de Dos Mundos"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 10, 31), "Día de las Iglesias Evangélicas y Protestantes"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 11, 1), "Día de Todos los Santos"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 12, 8), "Inmaculada Concepción"));
        specialDays.add(new SpecialDayEntity(specialDayId++, LocalDate.of(2023, 12, 25), "Navidad"));
        return specialDays;
    }

    private List<UserEntity> generateUsers(int count) {
        List<UserEntity> users = new ArrayList<>();
        Random random = new Random();
        long userId = 1; // ID inicial para usuarios

        for (int i = 0; i < count; i++) {
            String rut = String.format("%d-%d", random.nextInt(99999999), random.nextInt(10));
            String name = "User" + i;
            String lastName = "LastName" + i;
            String email = "user" + i + "@example.com";
            LocalDate birthDate = LocalDate.of(1990 + random.nextInt(30), 1 + random.nextInt(12), 1 + random.nextInt(28));
            users.add(new UserEntity(userId++, rut, name, lastName, email, birthDate));
        }
        return users;
    }

    private List<ReserveEntity> generateReserves(int count, List<UserEntity> users, List<TariffEntity> tariffs, List<SpecialDayEntity> specialDays) {
        List<ReserveEntity> reserves = new ArrayList<>();
        Random random = new Random();
        int reserveId = 1;

        // Generar reservas distribuidas en semanas y meses
        for (int i = 0; i < count; i++) {
            LocalDate randomDate = LocalDate.now().minusMonths(random.nextInt(6)).withDayOfMonth(1); // Mes aleatorio en los últimos 6 meses
            int weekOfMonth = random.nextInt(4); // Semana aleatoria del mes
            randomDate = randomDate.plusWeeks(weekOfMonth);

            // Determinar si la semana tendrá más de una reserva
            int reservationsInWeek = random.nextDouble() < 0.3 ? 3 : 1; // 30% de probabilidad de tener 3 reservas en una semana

            for (int j = 0; j < reservationsInWeek; j++) {
                LocalDate reserveDay = randomDate.plusDays(random.nextInt(7)); // Día aleatorio dentro de la semana

                // Determinar si es fin de semana o feriado
                boolean isWeekendOrHoliday = reserveDay.getDayOfWeek().getValue() >= 6 ||
                        specialDays.stream().anyMatch(specialDay -> specialDay.getDate().equals(reserveDay));

                // Establecer el rango de horas según el día
                int startHour = isWeekendOrHoliday ? 10 + random.nextInt(12) : 14 + random.nextInt(8); // Inicio dentro del rango permitido
                int duration = 1 + random.nextInt(3); // Duración de 1 a 3 horas
                int endHour = Math.min(startHour + duration, 22); // Asegurar que no exceda las 22:00

                LocalTime begin = LocalTime.of(startHour, 0);
                LocalTime finish = LocalTime.of(endHour, 0);

                TariffEntity tariff = tariffs.get(random.nextInt(tariffs.size()));

                int groupSize = 2 + random.nextInt(15); // Tamaño del grupo hasta 15
                Set<UserEntity> group = new HashSet<>();
                while (group.size() < groupSize) {
                    group.add(users.get(random.nextInt(users.size())));
                }

                ReserveEntity reserve = new ReserveEntity();
                reserve.setId((long) reserveId++); // Asignar ID único
                reserve.setDate(reserveDay);
                reserve.setBegin(begin);
                reserve.setFinish(finish);
                reserve.setGroup(group);
                reserve.setTariff(tariff);
                reserve.setFinalPrice(reserveService.calculateFinalPrice(reserve, reserveDay.getMonthValue())); // Cálculo del precio final
                reserves.add(reserve);
            }
        }
        return reserves;
    }
}