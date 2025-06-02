package microservice5.backend.repositories;


import microservice5.backend.dto.PersonaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
@FeignClient(name = "m2")
public interface DescPersonaClient {

    @PostMapping("/desctnumber/obtener-descuento")
    ResponseEntity<Double> obtenerDescuento(@RequestBody PersonaDTO personaDTO);
}
