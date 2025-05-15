package edu.mtisw.backend.services;

import edu.mtisw.backend.entities.TariffEntity;
import edu.mtisw.backend.repositories.TariffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class TariffServiceTest {
    @InjectMocks
    private TariffService tariffService;

    @Mock
    private TariffRepository tariffRepository;

    private TariffEntity tariff, tariff1, tariff2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tariff = new TariffEntity(1L, 10, 10, 15000.00, 30, 5.00, 20.00, 14250.00, 18000.00);
        tariff1 = new TariffEntity(2L, 15, 15, 20000.00, 35, 5.00, 20.00, 19000.00, 24000.00);
        tariff2 = new TariffEntity(3L, 20, 20, 25000.00, 40, 5.00, 20.00, 23750.00, 30000.00);
    }

    @Test
    void whenGetTariffs_thenReturnTariffList() {
        // Given
        List<TariffEntity> tariffs = new ArrayList<>();
        tariffs.add(tariff);
        when(tariffRepository.findAll()).thenReturn(tariffs);

        // When
        List<TariffEntity> result = tariffService.getTariffs();

        // Then
        assertThat(result).isEqualTo(tariffs);
        verify(tariffRepository, times(1)).findAll();
    }

    @Test
    void whenSaveTariff_thenReturnSavedTariff() {
        // Given
        when(tariffRepository.save(tariff)).thenReturn(tariff);

        // When
        TariffEntity result = tariffService.saveTariff(tariff);

        // Then
        assertThat(result).isEqualTo(tariff);
        verify(tariffRepository, times(1)).save(tariff);
    }

    @Test
    void whenGetTariffById_thenReturnTariff() {
        // Given
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));

        // When
        TariffEntity result = tariffService.getTariffById(1L);

        // Then
        assertThat(result).isEqualTo(tariff);
        verify(tariffRepository, times(1)).findById(1L);
    }

    @Test
    void whenDeleteTariff_thenVerifyDeletion() {
        // When
        tariffService.deleteTariff(1L);

        // Then
        verify(tariffRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenGetTariffByLaps_thenReturnTariff() {
        // Given
        when(tariffRepository.findByLaps(10)).thenReturn(tariff);

        // When
        TariffEntity result = tariffService.getTariffByLaps(10);

        // Then
        assertThat(result).isEqualTo(tariff);
        verify(tariffRepository, times(1)).findByLaps(10);
    }

    @Test
    void whenGetTariffByMaxMinutes_thenReturnTariff() {
        // Given
        when(tariffRepository.findByMaxMinutes(30)).thenReturn(tariff);

        // When
        TariffEntity result = tariffService.getTariffByMaxMinutes(30);

        // Then
        assertThat(result).isEqualTo(tariff);
        verify(tariffRepository, times(1)).findByMaxMinutes(30);
    }

    @Test
    void whenGetTariffByIdOrLapsOrMaxMinutes_thenReturnTariff() {
        // Given
        when(tariffRepository.findById(1L)).thenReturn(Optional.of(tariff));
        when(tariffRepository.findByLaps(20)).thenReturn(tariff2);
        when(tariffRepository.findByMaxMinutes(15)).thenReturn(tariff1);

        // When
        TariffEntity result1 = tariffService.getTariffByIdOrLapsOrMaxMinutes(1L, null, null);
        TariffEntity result2 = tariffService.getTariffByIdOrLapsOrMaxMinutes(null, 20, null);
        TariffEntity result3 = tariffService.getTariffByIdOrLapsOrMaxMinutes(null, null, 15);
        TariffEntity result4 = tariffService.getTariffByIdOrLapsOrMaxMinutes(null, null, null);

        // Then
        assertThat(result1).isEqualTo(tariff);
        assertThat(result2).isEqualTo(tariff2);
        assertThat(result3).isEqualTo(tariff1);
        assertThat(result4).isNull();
    }
}
