package microservice7.backend.repositories;

import microservice7.backend.dto.DesctNumberDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Repository
@FeignClient(name = "m2")
public interface DesctNumberClient {

    @GetMapping("/desctnumber/")
    List<DesctNumberDTO> getAllDesctNumbers();
}