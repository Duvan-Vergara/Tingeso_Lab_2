package microservice7.backend.repositories;

import microservice7.backend.dto.ReserveBasicDTO;
import microservice7.backend.dto.StarEndDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
@FeignClient(name = "m5")
public interface ReserveClient {

    @PostMapping("/reserves/getAll")
    List<ReserveBasicDTO> getAll(@RequestBody StarEndDTO tiempo);
}