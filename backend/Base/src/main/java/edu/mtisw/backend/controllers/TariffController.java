package edu.mtisw.backend.controllers;

import edu.mtisw.backend.entities.TariffEntity;
import edu.mtisw.backend.services.TariffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tariffs")
@CrossOrigin("*")
public class TariffController {
    @Autowired
    TariffService tariffService;

    @GetMapping("/")
    public ResponseEntity<List<TariffEntity>> listTariffs() {
        List<TariffEntity> tariffs = tariffService.getTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @PostMapping("/")
    public ResponseEntity<TariffEntity> saveTariff(@RequestBody TariffEntity tariff) {
        TariffEntity newTariff = tariffService.saveTariff(tariff);
        return ResponseEntity.ok(newTariff);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TariffEntity> getTariffById(@PathVariable Long id) {
        TariffEntity tariff = tariffService.getTariffById(id);
        return ResponseEntity.ok(tariff);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTariffById(@PathVariable Long id) {
        tariffService.deleteTariff(id);
        return ResponseEntity.noContent().build();
    }
}