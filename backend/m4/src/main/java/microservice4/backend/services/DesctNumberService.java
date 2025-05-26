package microservice4.backend.services;

import lombok.RequiredArgsConstructor;
import microservice4.backend.dto.DesctDTO;
import microservice4.backend.entities.DesctNumberEntity;
import microservice4.backend.repositories.DesctNumberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DesctNumberService {

    private final DesctNumberRepository desctNumberRepository;

    public double obtenerDescuento(int numeroPersonas) {
        DesctNumberEntity descuento = desctNumberRepository
                .findFirstByMinpersonasLessThanEqualAndMaxpersonasGreaterThanEqual(numeroPersonas, numeroPersonas);
        return descuento != null ? descuento.getPorcentajedesct() : 0.0;
    }

    public void guardar(DesctDTO desctDTO) {
        DesctNumberEntity entity;
        if (desctDTO.getId() != null) {
            entity = desctNumberRepository.findById(desctDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Descuento no encontrado"));
            entity.setMinpersonas(desctDTO.getMinpersonas());
            entity.setMaxpersonas(desctDTO.getMaxpersonas());
            entity.setPorcentajedesct(desctDTO.getPorcentajedesct());
        } else {
            entity = new DesctNumberEntity(
                    null, // ID will be generated automatically
                    desctDTO.getMinpersonas(),
                    desctDTO.getMaxpersonas(),
                    desctDTO.getPorcentajedesct()
            );
        }
        desctNumberRepository.save(entity);
    }

}
