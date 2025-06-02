package microservice1.backend.controllers;

import lombok.RequiredArgsConstructor;
import microservice1.backend.dto.FechaDTO;
import microservice1.backend.dto.MaxMinutesIdDTO;
import microservice1.backend.dto.TiempoDTO;
import microservice1.backend.entities.TariffEntity;
import microservice1.backend.services.TariffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tariffs")
@RequiredArgsConstructor
public class TariffController {

    private final TariffService tariffService;

    @GetMapping("/")
    public ResponseEntity<List<TariffEntity>> listTariffs() {
        List<TariffEntity> tariffs = tariffService.getTariffs();
        return ResponseEntity.ok(tariffs);
    }

    @PostMapping("/save")
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

    @PostMapping("/baseprice")
    public double getBasePrice(@RequestBody FechaDTO fecha) {
        return tariffService.getPrice(fecha.getFecha(), fecha.getIdTarifa());
    }

    @PostMapping("/getBestTariff")
    public MaxMinutesIdDTO getBestTariff(@RequestBody TiempoDTO tiempo) {
        return tariffService.getBestTariffid(tiempo.getMinutos());
    }
}