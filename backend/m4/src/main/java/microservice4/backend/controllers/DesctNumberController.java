package microservice4.backend.controllers;

import lombok.RequiredArgsConstructor;
import microservice4.backend.dto.DesctDTO;
import microservice4.backend.dto.PersonaDTO;
import microservice4.backend.services.DesctNumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/desctnumber")
@RequiredArgsConstructor
public class DesctNumberController {

    private final DesctNumberService desctNumberService;

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