package microservice2.backend.controllers;

import microservice2.backend.entities.DesctNumberEntity;
import microservice2.backend.services.DesctNumberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
@CrossOrigin("*")
public class DesctNumberController {

    DesctNumberService desctNumberService;

    @GetMapping("/")
    public ResponseEntity<List<DesctNumberEntity>> listTariffs() {
        List<DesctNumberEntity> tariffs = desctNumberService.getTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @PostMapping("/")
    public ResponseEntity<DesctNumberEntity> saveTariff(@RequestBody DesctNumberEntity tariff) {
        DesctNumberEntity newTariff = desctNumberService.saveTariff(tariff);
        return ResponseEntity.ok(newTariff);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesctNumberEntity> getTariffById(@PathVariable Long id) {
        DesctNumberEntity tariff = desctNumberService.getTariffById(id);
        return ResponseEntity.ok(tariff);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariffById(@PathVariable Long id) {
        desctNumberService.deleteTariff(id);
        return ResponseEntity.noContent().build();
    }
}
