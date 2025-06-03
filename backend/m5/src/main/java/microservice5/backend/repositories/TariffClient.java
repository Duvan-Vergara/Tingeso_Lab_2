package microservice5.backend.repositories;

import microservice5.backend.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(name = "m1")
public interface TariffClient {

    @PostMapping("/tariffs/baseprice")
    double getBasePrice(@RequestBody FechaDTO fecha);

    @PostMapping("/tariffs/getBestTariff")
    MaxMinutesIdDTO getBestTariffId(@RequestBody TiempoDTO tiempo);

    @PostMapping("/tariffs/getid")
    ResponseEntity<TariffBasicDTO> getTariffById(@RequestBody IdDTO id);
}
