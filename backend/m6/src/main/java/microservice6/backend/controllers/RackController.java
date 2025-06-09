package microservice6.backend.controllers;


import lombok.RequiredArgsConstructor;
import microservice6.backend.dto.MADDTO;
import microservice6.backend.services.RackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/racksemanal")
@RequiredArgsConstructor
public class RackController {

    private final RackService rackService;

    @PostMapping("/obtener")
    public ResponseEntity<List<List<String>>> obtener(@RequestBody MADDTO fecha) {
        List<List<String>> rackreserve = rackService.getReserveByWeek(fecha.getAnio(), fecha.getMes(), fecha.getDia());
        return ResponseEntity.ok(rackreserve);
    }

}
