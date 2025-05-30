package microservice1.backend.services;

import lombok.RequiredArgsConstructor;
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

    public double getPrice(LocalDate fecha){
        PrecioDTO precioDTO = new PrecioDTO();
        precioDTO.setFecha(fecha);
        //se debe buscar la  tarifa que quiero, asi que  debo recibir tambien la id de la tarifa
        precioDTO.setPrecioRegular(0.0);
        return tariffSpecialClient.obtenerPrecio(PrecioDTO);
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
