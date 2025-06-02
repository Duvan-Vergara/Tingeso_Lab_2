package microservice1.backend.services;

import lombok.RequiredArgsConstructor;
import microservice1.backend.dto.MaxMinutesIdDTO;
import microservice1.backend.dto.PrecioDTO;
import microservice1.backend.entities.TariffEntity;
import microservice1.backend.repositories.TariffRepository;
import microservice1.backend.repositories.TariffSpecialClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffService {

    private final TariffRepository tariffRepository;
    private final TariffSpecialClient tariffSpecialClient;

    public List<TariffEntity> getTariffs(){
        return new ArrayList<>(tariffRepository.findAll());
    }

    public TariffEntity saveTariff(TariffEntity tariff) {
        return tariffRepository.save(tariff);
    }

    public double getPrice(LocalDate fecha, Long idTariff) {
        TariffEntity tarifa = tariffRepository.findById(idTariff)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada con id: " + idTariff));
        PrecioDTO precioDTO = new PrecioDTO();
        precioDTO.setFecha(fecha);
        precioDTO.setPrecioRegular(tarifa.getRegular_price());
        return tariffSpecialClient.obtenerPrecio(precioDTO);
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

    public MaxMinutesIdDTO getBestTariffid(long minutos){
        MaxMinutesIdDTO maxMinutesIdDTO = new MaxMinutesIdDTO();
        TariffEntity bestTariff;
        List<TariffEntity> tarifasAdecuadas = tariffRepository.findFirstByTotalDurationGreaterThanEqualOrderByTotalDurationAsc(minutos);
        if (!tarifasAdecuadas.isEmpty()) {
            bestTariff = tarifasAdecuadas.get(0);
            maxMinutesIdDTO.setId(bestTariff.getId());
            maxMinutesIdDTO.setMaxMinutes(bestTariff.getMax_minutes());
        } else {
            List<TariffEntity> maxTarifa = tariffRepository.findTopByOrderByTotalDurationDesc();
            if (!maxTarifa.isEmpty()) {
                bestTariff = maxTarifa.get(0);
                maxMinutesIdDTO.setId(bestTariff.getId());
                maxMinutesIdDTO.setMaxMinutes(bestTariff.getMax_minutes());
            } else {
                throw new IllegalArgumentException("No se encontró una tarifa adecuada para " + minutos + " minutos.");
            }
        }
        return maxMinutesIdDTO;
    }

}
