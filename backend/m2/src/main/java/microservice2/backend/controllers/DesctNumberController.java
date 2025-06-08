package microservice2.backend.controllers;

import lombok.RequiredArgsConstructor;
import microservice2.backend.dto.DesctDTO;
import microservice2.backend.dto.PersonaDTO;
import microservice2.backend.entities.DesctNumberEntity;
import microservice2.backend.services.DesctNumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desctnumber")
@RequiredArgsConstructor
public class DesctNumberController {

    private final DesctNumberService desctNumberService;

    @GetMapping("/")
    public List<DesctNumberEntity> obtenerDescuentos() {
        return desctNumberService.obtenerTodos();
    }

    @PostMapping("/obtener-descuento")
    public ResponseEntity<Double> obtenerDescuento(@RequestBody PersonaDTO personaDTO) {
        double descuento = desctNumberService.obtenerDescuento(personaDTO.getPersonas());
        return ResponseEntity.ok(descuento);
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearDescuento(@RequestBody DesctDTO desctDTO) {
        desctNumberService.guardar(desctDTO);
        return ResponseEntity.ok().build();
    }

}