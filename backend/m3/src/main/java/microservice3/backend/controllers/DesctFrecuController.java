package microservice3.backend.controllers;

import lombok.RequiredArgsConstructor;
import microservice3.backend.dto.DesctDTO;
import microservice3.backend.dto.VecesDTO;
import microservice3.backend.entities.DesctFrecuEntity;
import microservice3.backend.services.DesctFrecuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desctfrecu")
@RequiredArgsConstructor
public class DesctFrecuController {

    private final DesctFrecuService desctFrecuService;

    @GetMapping("/")
    public List<DesctFrecuEntity> getall(){
        return desctFrecuService.getAll();
    }

    @PostMapping("/obtener-descuento")
    public ResponseEntity<Double> obtenerDescuento(@RequestBody VecesDTO vecesDTO) {
        double descuento = desctFrecuService.obtenerDescuento(vecesDTO.getVeces());
        return ResponseEntity.ok(descuento);
    }

    @PostMapping("/crear")
    public ResponseEntity<Void> crearDescuento(@RequestBody DesctDTO desctDTO) {
        desctFrecuService.guardar(desctDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/listar")
    public ResponseEntity<List<Double>> obtenerDescuentos(@RequestBody List<Integer> listaVeces) {
        List<Double> descuentos = desctFrecuService.obtenerDescuentos(listaVeces);
        return ResponseEntity.ok(descuentos);
    }

}