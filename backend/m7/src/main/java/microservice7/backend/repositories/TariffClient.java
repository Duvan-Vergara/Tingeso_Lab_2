package microservice7.backend.repositories;

import microservice7.backend.dto.TariffBasicIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Repository
@FeignClient(name = "m1")
public interface TariffClient {

    @GetMapping("/tariffs/getAll")
    List<TariffBasicIdDTO> getAllTariffs();
}
