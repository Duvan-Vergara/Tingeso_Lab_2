package edu.mtisw.backend.services;

import com.itextpdf.text.DocumentException;
import edu.mtisw.backend.entities.TariffEntity;
import edu.mtisw.backend.repositories.SpecialDayRepository;
import edu.mtisw.backend.repositories.TariffRepository;
import edu.mtisw.backend.utils.ComplementReserve;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import edu.mtisw.backend.entities.UserEntity;
import edu.mtisw.backend.entities.ReserveEntity;
import edu.mtisw.backend.repositories.ReserveRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class ReserveServiceTest {

    @InjectMocks
    private ReserveService reserveService;

    @Mock
    private ReserveRepository reserveRepository;

    @Mock
    private TariffService tariffService;

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private SpecialDayRepository specialDayRepository;

    @Mock
    private ComplementReserve complementReserve;

    private ReserveEntity reserve, reserve1, reserve2, reserve3, reserve4, reserve5;
    private UserEntity user, user2, user3, user4, user5, user6, user7, user8, user9, user10;
    private TariffEntity tariff1, tariff2, tariff3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(reserveService, "senderEmail", "duvanvch12@gmail.com");

        // Configurar un JavaMailSender real
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("duvanvch12@gmail.com");
        mailSender.setPassword("csybsewhwltytjlf");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        reserveService.javaMailSender = mailSender;

        // Configurar el mock para llamar a los métodos reales
        doCallRealMethod().when(complementReserve).calculateBirthdayLimit(anyInt());
        doCallRealMethod().when(complementReserve).calculateGroupSizeDiscount(anyInt());
        doCallRealMethod().when(complementReserve).calculateBestDiscount(any(ReserveEntity.class), anyList());
        doCallRealMethod().when(complementReserve).calculateFrequentCustomerDiscount(anyList());
        doCallRealMethod().when(complementReserve).isBirthday(any(UserEntity.class), any(LocalDate.class));

        user = new UserEntity(1L, "12.345.678-9", "Yugo", "Smith", "duvanvch12@gmail.com", LocalDate.of(1995, 5, 15));
        user2 = new UserEntity(2L, "98.765.432-1", "Anna", "Johnson", "duvanvch12@gmail.com", LocalDate.of(1990, 8, 20));
        user3 = new UserEntity(3L, "11.223.344-5", "Carlos", "Gomez", "duvanvch12@gmail.com", LocalDate.of(1988, 12, 10));
        user4 = new UserEntity(4L, "22244333-5", "User4", "LastName4", "duvanvch12@gmail.com", LocalDate.of(2004, 2, 1));
        user5 = new UserEntity(5L, "58176622-0", "User5", "LastName5", "duvanvch12@gmail.com", LocalDate.of(2002, 7, 6));
        user6 = new UserEntity(6L, "98877299-5", "User6", "LastName6", "duvanvch12@gmail.com", LocalDate.of(2008, 4, 22));
        user7 = new UserEntity(7L, "9190820-5", "User7", "LastName7", "duvanvch12@gmail.com", LocalDate.of(2002, 9, 7));
        user8 = new UserEntity(8L, "63106613-7", "User8", "LastName8", "duvanvch12@gmail.com", LocalDate.of(1997, 11, 3));
        user9 = new UserEntity(9L, "35709765-2", "User9", "LastName9", "duvanvch12@gmail.com", LocalDate.of(2008, 11, 22));
        user10 = new UserEntity(10L, "6021623-0", "User10", "LastName10", "duvanvch12@gmail.com", LocalDate.of(2000, 12, 22));


        Set<UserEntity> group = new LinkedHashSet<>();
        group.add(user);
        group.add(user2);
        group.add(user3);

        Set<UserEntity> group1 = new LinkedHashSet<>();
        group1.add(user);
        group1.add(user2);
        group1.add(user3);

        Set<UserEntity> group2 = new LinkedHashSet<>();
        group2.add(user4);
        group2.add(user5);
        group2.add(user6);

        Set<UserEntity> group3 = new LinkedHashSet<>();
        group3.add(user7);
        group3.add(user8);
        group3.add(user9);
        group3.add(user10);

        tariff1 = new TariffEntity(1L, 10, 10, 15000.00, 30, 5.00, 20.00, 14250.00, 18000.00);
        tariff2 = new TariffEntity(2L, 15, 15, 20000.00, 35, 5.00, 20.00, 19000.00, 24000.00);
        tariff3 = new TariffEntity(3L, 20, 20, 25000.00, 40, 5.00, 20.00, 23750.00, 30000.00);
        when(tariffRepository.findAll()).thenReturn(new ArrayList<>(List.of(tariff1, tariff2, tariff3)));

        reserve1 = new ReserveEntity();
        reserve1.setId(2L);
        reserve1.setDate(LocalDate.of(2023, 12, 20));
        reserve1.setBegin(LocalTime.of(17, 0));
        reserve1.setFinish(LocalTime.of(17, 30));
        reserve1.setGroup(group1);
        reserve1.setTariff(tariff1);
        reserve1.setFinalPrice(0.0);

        reserve = new ReserveEntity();
        reserve.setId(1L);
        reserve.setDate(LocalDate.now());
        reserve.setBegin(LocalTime.now());
        reserve.setFinish(reserve.getBegin().plusMinutes(30));
        reserve.setGroup(group);
        reserve.setTariff(tariff1);
        reserve.setFinalPrice(0.0);

        reserve2 = new ReserveEntity();
        reserve2.setId(3L);
        reserve2.setDate(LocalDate.of(2023, 12, 20));
        reserve2.setBegin(LocalTime.of(17, 0));
        reserve2.setFinish(LocalTime.of(17, 30));
        reserve2.setGroup(group1);
        reserve2.setTariff(tariff1);
        reserve2.setFinalPrice(0.0);


        reserve3 = new ReserveEntity();
        reserve3.setId(4L);
        reserve3.setDate(LocalDate.of(2025, 4, 8));
        reserve3.setBegin(LocalTime.of(17, 0));
        reserve3.setFinish(LocalTime.of(17, 35));
        reserve3.setGroup(group1);
        //reserve3.setTariff(tariff1);
        //reserve3.setFinalPrice(0.0);

        reserve4 = new ReserveEntity();
        reserve4.setId(5L);
        reserve4.setDate(LocalDate.of(2025, 4, 9));
        reserve4.setBegin(LocalTime.of(17, 0));
        reserve4.setFinish(LocalTime.of(17, 30));
        reserve4.setGroup(group2);
        //reserve4.setTariff(tariff1);
        //reserve4.setFinalPrice(0.0);

        reserve5 = new ReserveEntity();
        reserve5.setId(6L);
        reserve5.setDate(LocalDate.of(2025, 4, 10));
        reserve5.setBegin(LocalTime.of(17, 0));
        reserve5.setFinish(LocalTime.of(17, 40));
        reserve5.setGroup(group3);
        //reserve5.setTariff(tariff1);
        //reserve5.setFinalPrice(0.0);

    }

    private boolean isSpecialDay(LocalDate date) {
        List<LocalDate> specialDays = List.of(
                LocalDate.of(date.getYear(), 1, 1), // Año Nuevo
                LocalDate.of(date.getYear(), 4, 18), // Viernes Santo
                LocalDate.of(date.getYear(), 4, 19), // Sábado Santo
                LocalDate.of(date.getYear(), 5, 1), // Día Nacional del Trabajo
                LocalDate.of(date.getYear(), 5, 21), // Día de las Glorias Navales
                LocalDate.of(date.getYear(), 6, 20), // Día Nacional de los Pueblos Indígenas
                LocalDate.of(date.getYear(), 6, 29), // San Pedro y San Pablo
                LocalDate.of(date.getYear(), 7, 16), // Día de la Virgen del Carmen
                LocalDate.of(date.getYear(), 8, 15), // Asunción de la Virgen
                LocalDate.of(date.getYear(), 9, 18), // Independencia Nacional
                LocalDate.of(date.getYear(), 9, 19), // Día de las Glorias del Ejército
                LocalDate.of(date.getYear(), 10, 12), // Encuentro de Dos Mundos
                LocalDate.of(date.getYear(), 10, 31), // Día de las Iglesias Evangélicas y Protestantes
                LocalDate.of(date.getYear(), 11, 1), // Día de Todos los Santos
                LocalDate.of(date.getYear(), 12, 8), // Inmaculada Concepción
                LocalDate.of(date.getYear(), 12, 25), // Navidad
                // Feriados específicos
                LocalDate.of(date.getYear(), 6, 7), // Asalto y Toma del Morro de Arica
                LocalDate.of(date.getYear(), 8, 20), // Nacimiento del Prócer de la Independencia
                LocalDate.of(date.getYear(), 12, 31) // Feriado Bancario
        );
        return specialDays.contains(date) || date.getDayOfWeek().getValue() == 7; // Incluye todos los domingos
    }

    @Test
    void whenGetReserves_thenReturnList() {
        // Given
        List<ReserveEntity> reserves = List.of(reserve);
        when(reserveRepository.findAll()).thenReturn(reserves);

        // When
        List<ReserveEntity> result = reserveService.getReserves();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(reserveRepository, times(1)).findAll();
    }

    @Test
    void whenSaveReserve_thenReturnSavedReserve() {
        // Given
        when(reserveRepository.save(reserve)).thenReturn(reserve);

        // When
        ReserveEntity result = reserveService.saveReserve(reserve);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reserve.getId());
        verify(reserveRepository, times(1)).save(reserve);
    }

    @Test
    void whenSaveReserveWithoutTariff_thenCalculateTariff() {
        // Given
        reserve.setTariff(null);
        when(tariffRepository.findAll()).thenReturn(new ArrayList<>(List.of(tariff1, tariff2, tariff3)));
        when(reserveRepository.save(reserve)).thenReturn(reserve);
        // When
        ReserveEntity result = reserveService.saveReserve(reserve);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTariff()).isEqualTo(tariff1); // Verifica que se asignó la tarifa correcta
        verify(tariffRepository, times(1)).findAll();
    }

    @Test
    void whenGetReserveById_thenReturnReserve() {
        // Given
        when(reserveRepository.findById(1L)).thenReturn(Optional.of(reserve));

        // When
        ReserveEntity result = reserveService.getReserveById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(reserveRepository, times(1)).findById(1L);
    }

    @Test
    void whenGetTariffForDateOnSpecialDay_thenReturnHolidayPrice() {
        // Given
        LocalDate specialDay = LocalDate.of(2023, 12, 25); // Navidad
        reserve.setDate(specialDay);
        when(complementReserve.isSpecialDay(specialDay)).thenReturn(true);

        // When
        double result = reserveService.getTariffForDate(reserve);

        // Then
        assertThat(result).isEqualTo(reserve.getTariff().getHolidayPrice());
    }

    @Test
    void whenCalculateTariffForReserveWithDurationGreaterThanLongestTariff_thenReturnLongestTariff() {
        // Given
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM
        LocalTime endTime = startTime.plusMinutes(60); // 11:00 AM
        List<TariffEntity> tariffs = new ArrayList<>(List.of(tariff1, tariff2, tariff3));

        // When
        TariffEntity result = reserveService.calculateTariffForReserve(startTime, endTime, tariffs);

        // Then
        assertThat(result).isEqualTo(tariff3); // Tarifa más larga
    }

    @Test
    void whenCalculateTariffForReserveWithDurationBetweenTariffs_thenReturnCorrectTariff_2() {
        // Given
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM;
        LocalTime endTime = startTime.plusMinutes(2); // 2 minutos
        List<TariffEntity> tariffs = new ArrayList<>(List.of(tariff1, tariff2, tariff3));

        // When
        TariffEntity result = reserveService.calculateTariffForReserve(startTime, endTime, tariffs);

        // Then
        assertThat(result).isEqualTo(tariff1); // Tarifa adecuada
    }

    @Test
    void whenCalculateTariffForReserveWithDurationBetweenTariffs_thenReturnCorrectTariff() {
        // Given
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM
        LocalTime endTime = startTime.plusMinutes(35); // 35 minutos
        List<TariffEntity> tariffs = new ArrayList<>(List.of(tariff1, tariff2, tariff3));

        // When
        TariffEntity result = reserveService.calculateTariffForReserve(startTime, endTime, tariffs);

        // Then
        assertThat(result).isEqualTo(tariff2); // Tarifa adecuada
    }

    @Test
    void whenCalculateTariffForReserveWithNoSuitableTariff_thenThrowException() {
        // Given
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM
        LocalTime endTime = startTime.plusMinutes(1); // 1 minutos
        List<TariffEntity> tariffs = new ArrayList<>(); // Sin tarifas

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> reserveService.calculateTariffForReserve(startTime, endTime, tariffs));
    }

    @Test
    void whenGetTariffForDateOnWeekend_thenReturnWeekendPrice() {
        // Given
        LocalDate weekendDay = LocalDate.of(2023, 12, 23); // Sábado
        reserve.setDate(weekendDay);
        when(complementReserve.isSpecialDay(weekendDay)).thenReturn(false);
        when(complementReserve.isWeekend(weekendDay)).thenReturn(true);

        // When
        double result = reserveService.getTariffForDate(reserve);

        // Then
        assertThat(result).isEqualTo(reserve.getTariff().getWeekendPrice());
    }

    @Test
    void whenDurationFallsBetweenTwoTariffs_thenReturnClosestHigherTariff() {
        // Given
        LocalTime startTime = LocalTime.of(10, 0); // 10:00 AM
        LocalTime endTime = startTime.plusMinutes(41); // 41 minutos
        List<TariffEntity> tariffs = new ArrayList<>(List.of(tariff1, tariff2, tariff3));

        // When
        TariffEntity result = reserveService.calculateTariffForReserve(startTime, endTime, tariffs);

        // Then
        assertThat(result).isEqualTo(tariff3); // Tarifa más cercana hacia arriba
    }

    @Test
    void whenDeleteReserveById_thenVerifyDeletion() throws Exception {
        // When
        boolean result = reserveService.deleteReserveById(1L);

        // Then
        assertThat(result).isTrue();
        verify(reserveRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteReserveById_thenThrowException() {
        // Given
        doThrow(new RuntimeException("Error al eliminar la reserva")).when(reserveRepository).deleteById(-1L);

        // When
        Exception exception = assertThrows(Exception.class, () -> reserveService.deleteReserveById(-1L));

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Error al eliminar la reserva");
        verify(reserveRepository, times(1)).deleteById(-1L);
    }

    @Test
    void whenGetReserveByDay_thenReturnList() {
        // Given
        List<ReserveEntity> reserves = List.of(reserve);
        when(reserveRepository.getReserveByDate_Day(1)).thenReturn(reserves);

        // When
        List<ReserveEntity> result = reserveService.getReserveByDay(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(reserveRepository, times(1)).getReserveByDate_Day(1);
    }

    @Test
    void whenGetReserveByMonth_thenReturnList() {
        // Given
        List<ReserveEntity> reserves = List.of(reserve);
        when(reserveRepository.getReserveByDate_Month(1)).thenReturn(reserves);

        // When
        List<ReserveEntity> result = reserveService.getReserveByMonth(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(reserveRepository, times(1)).getReserveByDate_Month(1);
    }

    @Test
    void whenGetReserveByMonthAndRut_thenReturnList() {
        // Given
        List<ReserveEntity> reserves = List.of(reserve);
        when(reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve.getDate().getMonthValue() + 1)).thenReturn(reserves);

        // When
        List<ReserveEntity> result = reserveService.getReservesByDate_MonthANDRut(user.getRut(), reserve.getDate().getMonthValue() + 1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        verify(reserveRepository, times(1)).getReservesByDateMonthAndRut(user.getRut(), reserve.getDate().getMonthValue() + 1);
    }

    @Test
    void whenCalculateFinalPrice_thenReturnCorrectPrice() {
        // Given
        when(reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve.getDate().getMonthValue() + 1))
                .thenReturn(List.of());

        // When
        double finalPrice = reserveService.calculateFinalPrice(reserve, reserve.getDate().getMonthValue() + 1);
        // Then
        assertThat(finalPrice).isEqualTo(40500.0);
    }


    @Test
    void whenCalculateFinalPrice2_thenReturnCorrectPrice() {
        // Given
        // Simular que el usuario tiene una reserva previa en el mes
        when(reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve1.getDate().getMonthValue() + 1))
                .thenReturn(List.of(reserve1));

        // Calcular el precio base según la tarifa
        double basePrice = tariff1.getRegularPrice();

        // Calcular el descuento por tamaño del grupo
        double groupDiscount = reserveService.complementReserve.calculateGroupSizeDiscount(reserve1.getGroup().size());
        List<Double> descuentos = new ArrayList<>();
        double descfrecuent;
        // Calcular el descuento por cliente frecuente
        for (UserEntity u : reserve1.getGroup()) {
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(u.getRut(), reserve1.getDate().getMonthValue() + 1);
            descfrecuent = reserveService.complementReserve.calculateFrequentCustomerDiscount(userReserves);
            descuentos.add(Math.max(groupDiscount, descfrecuent));
        }

        // Calcular el precio final esperado
        double expectedPrice = 0.0;
        for (double d : descuentos) {
            expectedPrice += basePrice * (1 - d);
        }

        // When
        double finalPrice = reserveService.calculateFinalPrice(reserve1, reserve1.getDate().getMonthValue() + 1);

        // Then
        assertThat(finalPrice).isEqualTo(expectedPrice);
    }

    @Test
    void whenCalculateFinalPrice3_thenReturnCorrectPrice() {
        // Given
        // Simular que el usuario tiene una reserva previa en el mes
        when(reserveRepository.getReservesByDateMonthAndRut(user.getRut(), reserve1.getDate().getMonthValue() + 1))
                .thenReturn(List.of(reserve1, reserve));

        // Calcular el precio base según la tarifa
        double basePrice = tariff1.getRegularPrice();

        // Calcular el descuento por tamaño del grupo
        double groupDiscount = reserveService.complementReserve.calculateGroupSizeDiscount(reserve1.getGroup().size());
        List<Double> descuentos = new ArrayList<>();
        double descfrecuent;
        // Calcular el descuento por cliente frecuente
        for (UserEntity u : reserve1.getGroup()) {
            List<ReserveEntity> userReserves = reserveRepository.getReservesByDateMonthAndRut(u.getRut(), reserve1.getDate().getMonthValue() + 1);
            descfrecuent = reserveService.complementReserve.calculateFrequentCustomerDiscount(userReserves);
            descuentos.add(Math.max(groupDiscount, descfrecuent));
        }

        // Calcular el precio final esperado
        double expectedPrice = 0.0;
        for (double d : descuentos) {
            expectedPrice += basePrice * (1 - d);
        }

        // When
        double finalPrice = reserveService.calculateFinalPrice(reserve1, reserve1.getDate().getMonthValue() + 1);

        // Then
        assertThat(finalPrice).isEqualTo(expectedPrice);
    }

    @Test
    void whenCalculateFinalPriceWithBirthdayUser_thenApplyBirthdayDiscount() {
        // Given
        LocalDate birthday = LocalDate.now();
        user.setBirthDate(LocalDate.of(2000, 12, 20));
        reserve1.setGroup(Set.of(user));
        when(complementReserve.isBirthday(user, reserve1.getDate())).thenReturn(true);
        when(complementReserve.calculateBirthdayLimit(1)).thenReturn(1);

        // When
        double result = reserveService.calculateFinalPrice(reserve1, reserve1.getDate().getMonthValue() + 1);


        // Then
        assertThat(result).isEqualTo(reserve1.getTariff().getRegularPrice() * 0.5);
    }

    @Test
    void whenGetReserveByWeek_thenReturnGroupedReserves() {
        // Given
        int year = 2025;
        int month = 4;
        int day = 10; // Jueves de la semana pasada

        // Simular las reservas en la semana
        List<ReserveEntity> reserves = List.of(reserve3, reserve4, reserve5);
        when(reserveRepository.getReserveByDate_DateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reserves);

        // When
        List<List<String>> result = reserveService.getReserveByWeek(year, month, day);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(7); // Una lista para cada día de la semana

        // Verificar que las reservas están agrupadas correctamente
        assertThat(result.get(0)).isEmpty(); // Lunes (7 de abril)
        assertThat(result.get(1)).contains("Yugo (17:00 - 17:35)"); // Martes (8 de abril)
        assertThat(result.get(2)).contains("User4 (17:00 - 17:30)"); // Miércoles (9 de abril)
        assertThat(result.get(3)).contains("User7 (17:00 - 17:40)"); // Jueves (10 de abril)
        assertThat(result.get(4)).isEmpty(); // Viernes (11 de abril)
        assertThat(result.get(5)).isEmpty(); // Sábado (12 de abril)
        assertThat(result.get(6)).isEmpty(); // Domingo (13 de abril)
    }

    @Test
    void whenGetReserveByWeekWithMultipleReserves_thenReturnGroupedAndOrderedReserves() {
        // Given
        int year = 2025;
        int month = 4;
        int day = 10; // Jueves de la semana pasada

        // Crear múltiples reservas para cada día de la semana
        List<ReserveEntity> reserves = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.of(2025, 4, 7).plusDays(i); // Semana del 7 al 13 de abril
            for (int j = 0; j < 3; j++) { // Tres reservas por día
                ReserveEntity reserve = new ReserveEntity();
                reserve.setId((long) (i * 3 + j + 1));
                reserve.setDate(date);
                reserve.setBegin(LocalTime.of(9 + j, 0)); // 9:00, 10:00, 11:00
                reserve.setFinish(LocalTime.of(9 + j, 30)); // 9:30, 10:30, 11:30
                UserEntity user = new UserEntity((long) (i * 3 + j + 1), "RUT-" + (i * 3 + j + 1), "User" + (i * 3 + j + 1), "LastName" + (i * 3 + j + 1), "user" + (i * 3 + j + 1) + "@example.com", LocalDate.of(1990, 1, 1).plusDays(i * 3 + j));
                reserve.setGroup(Set.of(user));
                reserves.add(reserve);
            }
        }

        // Simular el repositorio para devolver las reservas
        when(reserveRepository.getReserveByDate_DateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reserves);

        // When
        List<List<String>> result = reserveService.getReserveByWeek(year, month, day);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(7); // Una lista para cada día de la semana

        // Verificar que las reservas están agrupadas correctamente y ordenadas
        for (int i = 0; i < 7; i++) {
            List<String> dayReserves = result.get(i);
            assertThat(dayReserves).isNotNull();
            assertThat(dayReserves.size()).isEqualTo(3); // Tres reservas por día
            for (int j = 0; j < dayReserves.size(); j++) {
                String expected = "User" + (i * 3 + j + 1) + " (" + (9 + j) + ":00 - " + (9 + j) + ":30)";
                assertThat(dayReserves.get(j)).isEqualTo(expected);
            }
        }
    }

    @Test
    void whenGeneratePaymentReceipt_thenCorrect() throws IOException {
        // When
        byte[] receipt = reserveService.generatePaymentReceipt(reserve);

        // Then
        assertThat(receipt).isNotNull();
        assertThat(receipt.length).isGreaterThan(0);

        // Validar contenido del Excel
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(receipt))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Código de Reserva");
            assertThat((long) sheet.getRow(1).getCell(0).getNumericCellValue()).isEqualTo(reserve.getId());
        }
    }

    @Test
    void whenConvertExcelToPdf_thenCorrect() throws IOException, DocumentException {
        // Given
        byte[] excelData = reserveService.generatePaymentReceipt(reserve);

        // When
        byte[] pdfData = reserveService.convertExcelToPdf(excelData);

        // Then
        assertThat(pdfData).isNotNull();
        assertThat(pdfData.length).isGreaterThan(0);
    }

    @Test
    void whenCreateJavaMailSender_thenCorrect() {
        // Configurar un JavaMailSenderImpl real
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("duvanvch12@gmail.com");
        mailSender.setPassword("csybsewhwltytjlf");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Simular el comportamiento del metodo createJavaMailSender
        ReserveService reserveService = new ReserveService();
        reserveService.javaMailSender = mailSender; // Inyectar manualmente el JavaMailSender

        // When
        JavaMailSender result = reserveService.createJavaMailSender();

        // Then
        assertThat(result).isNotNull();
        JavaMailSenderImpl impl = (JavaMailSenderImpl) result;
        assertThat(impl.getHost()).isEqualTo("smtp.gmail.com");
        assertThat(impl.getPort()).isEqualTo(587);
        assertThat(impl.getUsername()).isEqualTo("duvanvch12@gmail.com");
        assertThat(impl.getPassword()).isEqualTo("csybsewhwltytjlf");
        assertThat(impl.getJavaMailProperties().getProperty("mail.smtp.auth")).isEqualTo("true");
        assertThat(impl.getJavaMailProperties().getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
    }

    @Test
    void whenSendEmailWithAttachment_thenNoExceptions() throws IOException, DocumentException {
        // Crear datos de prueba
        byte[] pdfData = reserveService.convertExcelToPdf(reserveService.generatePaymentReceipt(reserve));

        // Enviar correo
        reserveService.sendEmailWithAttachment(
                "duvanvch12@gmail.com",
                "Asunto de Prueba",
                "Este es un correo de prueba con un archivo adjunto.",
                pdfData,
                "archivo_prueba.pdf"
        );
    }

    @Test
    void whenSendPaymentReceipts_thenEmailsSent() throws IOException, DocumentException {
        // Given
        reserve.setGroup(Set.of(user));
        reserve.setTariff(tariff1);

        // When
        reserveService.sendPaymentReceipts(reserve);
    }

    @Test
    void whenSendPaymentReceipts_2_thenEmailsSent() throws Exception {
        // Given
        reserve.setGroup(Set.of(user, user2, user3)); // Simular un grupo de usuarios
        reserve.setTariff(tariff1);

        // When
        reserveService.sendPaymentReceipts_2(reserve);
    }

    @Test
    void testGenerateTariffReport_SuccessWithMultipleMonths() throws IOException {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        TariffEntity tariff1 = new TariffEntity(1L, 10, 10, 15000.0, 30, 5.0, 20.0, 14250.0, 18000.0);
        TariffEntity tariff2 = new TariffEntity(2L, 15, 15, 22000.0, 45, 5.0, 20.0, 20900.0, 26400.0);
        TariffEntity tariff3 = new TariffEntity(3L, 20, 20, 28000.0, 60, 5.0, 20.0, 26600.0, 33600.0);

        when(tariffRepository.findAll()).thenReturn(Arrays.asList(tariff1, tariff2, tariff3));

        // Crear reservas para varios meses
        List<ReserveEntity> reserves = new ArrayList<>();

        // Enero: 2 reservas con tarifa1, 1 con tarifa2, 1 con tarifa3
        ReserveEntity reserve1 = createReserve(tariff1, LocalDate.of(2024, 1, 5), 10000.0);
        ReserveEntity reserve2 = createReserve(tariff1, LocalDate.of(2024, 1, 15), 12000.0);
        ReserveEntity reserve3 = createReserve(tariff2, LocalDate.of(2024, 1, 20), 20000.0);
        ReserveEntity reserve4 = createReserve(tariff3, LocalDate.of(2024, 1, 25), 25000.0);

        // Febrero: 1 reserva con tarifa1, 2 con tarifa2, 1 con tarifa3
        ReserveEntity reserve5 = createReserve(tariff1, LocalDate.of(2024, 2, 5), 15000.0);
        ReserveEntity reserve6 = createReserve(tariff2, LocalDate.of(2024, 2, 15), 22000.0);
        ReserveEntity reserve7 = createReserve(tariff2, LocalDate.of(2024, 2, 20), 20000.0);
        ReserveEntity reserve8 = createReserve(tariff3, LocalDate.of(2024, 2, 25), 28000.0);

        // Marzo: 1 reserva con tarifa1, 1 con tarifa2, 2 con tarifa3
        ReserveEntity reserve9 = createReserve(tariff1, LocalDate.of(2024, 3, 5), 12000.0);
        ReserveEntity reserve10 = createReserve(tariff2, LocalDate.of(2024, 3, 15), 21000.0);
        ReserveEntity reserve11 = createReserve(tariff3, LocalDate.of(2024, 3, 20), 27000.0);
        ReserveEntity reserve12 = createReserve(tariff3, LocalDate.of(2024, 3, 25), 26000.0);

        reserves.addAll(Arrays.asList(reserve1, reserve2, reserve3, reserve4, reserve5, reserve6,
                reserve7, reserve8, reserve9, reserve10, reserve11, reserve12));

        when(reserveRepository.getReserveByDate_DateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reserves);

        // When
        byte[] report = reserveService.generateTariffReport(startDate, endDate);

        // Then
        assertThat(report).isNotNull();
        assertThat(report.length).isGreaterThan(0);

        // Validar contenido del Excel
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(report))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet).isNotNull();

            // 3 tarifas + 1 fila de totales
            assertThat(sheet.getLastRowNum()).isEqualTo(4);

            // Verificar encabezados
            Row headerRow = sheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Número de vueltas o tiempo máximo permitido");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("ENERO");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("FEBRERO");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("MARZO");
            assertThat(headerRow.getCell(4).getStringCellValue()).isEqualTo("TOTAL");

            // Verificar totales por tarifa
            Row tariff1Row = sheet.getRow(1);
            assertThat(tariff1Row.getCell(0).getStringCellValue()).contains("10 vueltas");
            assertThat(tariff1Row.getCell(1).getNumericCellValue()).isEqualTo(22000.0); // Enero
            assertThat(tariff1Row.getCell(2).getNumericCellValue()).isEqualTo(15000.0); // Febrero
            assertThat(tariff1Row.getCell(3).getNumericCellValue()).isEqualTo(12000.0); // Marzo
            assertThat(tariff1Row.getCell(4).getNumericCellValue()).isEqualTo(49000.0); // Total

            // Verificar totales generales
            Row totalRow = sheet.getRow(4);
            assertThat(totalRow.getCell(0).getStringCellValue()).isEqualTo("TOTAL");
            assertThat(totalRow.getCell(1).getNumericCellValue()).isEqualTo(67000.0); // Total Enero
            assertThat(totalRow.getCell(2).getNumericCellValue()).isEqualTo(85000.0); // Total Febrero
            assertThat(totalRow.getCell(3).getNumericCellValue()).isEqualTo(86000.0); // Total Marzo
            assertThat(totalRow.getCell(4).getNumericCellValue()).isEqualTo(238000.0); // Total General
        }
    }

    @Test
    void testGenerateGroupSizeReport_SuccessWithMultipleMonths() throws IOException {
        // Given
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        List<ReserveEntity> reserves = new ArrayList<>();

        // Enero: diferentes tamaños de grupo
        reserves.add(createReserveWithGroup(1, LocalDate.of(2024, 1, 5), 5000.0));
        reserves.add(createReserveWithGroup(2, LocalDate.of(2024, 1, 10), 8000.0));
        reserves.add(createReserveWithGroup(4, LocalDate.of(2024, 1, 15), 15000.0));
        reserves.add(createReserveWithGroup(7, LocalDate.of(2024, 1, 20), 20000.0));
        reserves.add(createReserveWithGroup(12, LocalDate.of(2024, 1, 25), 30000.0));

        // Febrero: diferentes tamaños de grupo
        reserves.add(createReserveWithGroup(2, LocalDate.of(2024, 2, 5), 7000.0));
        reserves.add(createReserveWithGroup(3, LocalDate.of(2024, 2, 10), 10000.0));
        reserves.add(createReserveWithGroup(6, LocalDate.of(2024, 2, 15), 18000.0));
        reserves.add(createReserveWithGroup(9, LocalDate.of(2024, 2, 20), 25000.0));
        reserves.add(createReserveWithGroup(14, LocalDate.of(2024, 2, 25), 35000.0));

        // Marzo: diferentes tamaños de grupo
        reserves.add(createReserveWithGroup(1, LocalDate.of(2024, 3, 5), 4000.0));
        reserves.add(createReserveWithGroup(5, LocalDate.of(2024, 3, 10), 16000.0));
        reserves.add(createReserveWithGroup(8, LocalDate.of(2024, 3, 15), 22000.0));
        reserves.add(createReserveWithGroup(11, LocalDate.of(2024, 3, 20), 28000.0));
        reserves.add(createReserveWithGroup(15, LocalDate.of(2024, 3, 25), 38000.0));

        when(reserveRepository.getReserveByDate_DateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(reserves);

        // When
        byte[] report = reserveService.generateGroupSizeReport(startDate, endDate);

        // Then
        assertThat(report).isNotNull();

        // Validar contenido del Excel
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(report))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertThat(sheet).isNotNull();

            // Verificar número correcto de filas: 4 categorías + 1 fila de totales
            assertThat(sheet.getLastRowNum()).isEqualTo(5);

            // Verificar encabezados
            Row headerRow = sheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("Número de personas");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("ENERO");
            assertThat(headerRow.getCell(2).getStringCellValue()).isEqualTo("FEBRERO");
            assertThat(headerRow.getCell(3).getStringCellValue()).isEqualTo("MARZO");
            assertThat(headerRow.getCell(4).getStringCellValue()).isEqualTo("TOTAL");

            // Verificar totales por categoría
            // Categoría 1-2 personas
            Row cat1Row = sheet.getRow(1);
            assertThat(cat1Row.getCell(0).getStringCellValue()).isEqualTo("1-2 personas");
            assertThat(cat1Row.getCell(1).getNumericCellValue()).isEqualTo(13000.0); // Enero
            assertThat(cat1Row.getCell(2).getNumericCellValue()).isEqualTo(7000.0);  // Febrero
            assertThat(cat1Row.getCell(3).getNumericCellValue()).isEqualTo(4000.0);  // Marzo

            // Categoría 3-5 personas
            Row cat2Row = sheet.getRow(2);
            assertThat(cat2Row.getCell(0).getStringCellValue()).isEqualTo("3-5 personas");
            assertThat(cat2Row.getCell(1).getNumericCellValue()).isEqualTo(15000.0); // Enero
            assertThat(cat2Row.getCell(2).getNumericCellValue()).isEqualTo(10000.0); // Febrero
            assertThat(cat2Row.getCell(3).getNumericCellValue()).isEqualTo(16000.0); // Marzo

            // Verificar totales generales
            Row totalRow = sheet.getRow(5);
            assertThat(totalRow.getCell(0).getStringCellValue()).isEqualTo("TOTAL");
            assertThat(totalRow.getCell(4).getNumericCellValue()).isEqualTo(281000.0); // Total General
        }
    }

    @Test
    void testGenerateTariffReport_InvalidDateRange() {
        // Given - Fecha de inicio posterior a fecha fin
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        when(tariffRepository.findAll()).thenReturn(List.of(
                new TariffEntity(1L, 10, 10, 15000.0, 30, 5.0, 20.0, 14250.0, 18000.0)
        ));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserveService.generateTariffReport(startDate, endDate);
        });

        assertThat(exception.getMessage()).isEqualTo("La fecha de inicio no puede ser posterior a la fecha fin.");
    }

    @Test
    void testGenerateGroupSizeReport_InvalidDateRange() {
        // Given - Fecha de inicio posterior a fecha fin
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserveService.generateGroupSizeReport(startDate, endDate);
        });

        assertThat(exception.getMessage()).isEqualTo("La fecha de inicio no puede ser posterior a la fecha fin.");
    }

    // Métodos auxiliares para crear reservas de prueba
    private ReserveEntity createReserve(TariffEntity tariff, LocalDate date, double finalPrice) {
        ReserveEntity reserve = new ReserveEntity();
        reserve.setTariff(tariff);
        reserve.setDate(date);
        reserve.setFinalPrice(finalPrice);
        return reserve;
    }

    private ReserveEntity createReserveWithGroup(int groupSize, LocalDate date, double finalPrice) {
        ReserveEntity reserve = new ReserveEntity();
        reserve.setDate(date);
        reserve.setFinalPrice(finalPrice);

        // Crear grupo de usuarios del tamaño especificado
        Set<UserEntity> group = new HashSet<>();
        for (int i = 0; i < groupSize; i++) {
            UserEntity user = new UserEntity();
            user.setId((long) i);
            user.setName("Usuario " + i);
            user.setRut("1234567" + i);
            group.add(user);
        }
        reserve.setGroup(group);

        // Asignar tarifa según el tamaño del grupo
        TariffEntity tariff = new TariffEntity();
        tariff.setId(1L);
        tariff.setLaps(10);
        tariff.setMaxMinutes(10);
        reserve.setTariff(tariff);

        return reserve;
    }

    @Test
    void testGenerateTariffReport_NoTariffsRegistered() {
        // Given - Fechas válidas pero sin tarifas registradas
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 31);

        // Simular que no hay tarifas registradas
        when(tariffRepository.findAll()).thenReturn(new ArrayList<>());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reserveService.generateTariffReport(startDate, endDate);
        });

        assertThat(exception.getMessage()).isEqualTo("No existen tarifas registradas");
    }


}


