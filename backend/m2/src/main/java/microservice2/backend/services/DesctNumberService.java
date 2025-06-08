package microservice2.backend.services;

import lombok.RequiredArgsConstructor;
import microservice2.backend.dto.DesctDTO;
import microservice2.backend.entities.DesctNumberEntity;
import microservice2.backend.repositories.DesctNumberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DesctNumberService {

    private final DesctNumberRepository desctNumberRepository;

    public List<DesctNumberEntity> obtenerTodos() {
        return desctNumberRepository.findAll();
    }

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
