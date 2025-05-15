package edu.mtisw.backend.services;

import edu.mtisw.backend.entities.SpecialDayEntity;
import edu.mtisw.backend.repositories.SpecialDayRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecialDayServiceTest {

    @InjectMocks
    private SpecialDayService specialDayService;

    @Mock
    private SpecialDayRepository specialDayRepository;

    private SpecialDayEntity specialDay;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        specialDay = new SpecialDayEntity(1L, LocalDate.of(2023, 12, 25), "Navidad");
    }

    @Test
    void whenGetSpecialDays_thenReturnSpecialDayList() {
        // Given
        List<SpecialDayEntity> specialDays = new ArrayList<>();
        specialDays.add(specialDay);
        when(specialDayRepository.findAll()).thenReturn(specialDays);

        // When
        List<SpecialDayEntity> result = specialDayService.getSpecialDays();

        // Then
        assertThat(result).isEqualTo(specialDays);
    }

    @Test
    void whenSaveSpecialDay_thenReturnSavedSpecialDay() {
        // Given
        when(specialDayRepository.save(specialDay)).thenReturn(specialDay);

        // When
        SpecialDayEntity result = specialDayService.saveSpecialDay(specialDay);

        // Then
        assertThat(result).isEqualTo(specialDay);
    }

    @Test
    void whenGetSpecialDayById_thenReturnSpecialDay() {
        // Given
        when(specialDayRepository.findById(1L)).thenReturn(Optional.of(specialDay));

        // When
        SpecialDayEntity result = specialDayService.getSpecialDayById(1L);

        // Then
        assertThat(result).isEqualTo(specialDay);
    }

    @Test
    void whenDeleteSpecialDayById_thenReturnTrue() throws Exception {
        // Given
        doNothing().when(specialDayRepository).deleteById(1L);

        // When
        boolean result = specialDayService.deleteSpecialDayByID(1L);

        // Then
        assertThat(result).isTrue();
        verify(specialDayRepository, times(1)).deleteById(1L);
    }

    @Test
    void whenDeleteSpecialDayByIdThrowsException_thenCatchBlockExecutes() {
        // Given
        doThrow(new RuntimeException("Error al eliminar")).when(specialDayRepository).deleteById(1L);

        // When
        Exception exception = assertThrows(Exception.class, () -> specialDayService.deleteSpecialDayByID(1L));

        // Then
        assertThat(exception.getMessage()).isEqualTo("Error al eliminar");
        verify(specialDayRepository, times(1)).deleteById(1L);
    }
}