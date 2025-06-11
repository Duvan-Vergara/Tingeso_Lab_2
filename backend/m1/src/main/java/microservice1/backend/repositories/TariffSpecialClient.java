package microservice1.backend.repositories;

import microservice1.backend.dto.PrecioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(name = "m4")
public interface TariffSpecialClient {

    @PostMapping("/specialtariffs/obtenerprice")
    double obtenerPrecio(@RequestBody PrecioDTO precioDTO);
}
