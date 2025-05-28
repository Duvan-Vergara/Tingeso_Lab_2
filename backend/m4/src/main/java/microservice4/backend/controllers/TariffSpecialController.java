package microservice4.backend.controllers;

import lombok.AllArgsConstructor;
import microservice4.backend.dto.PrecioDTO;
import microservice4.backend.dto.TarifaDTO;
import microservice4.backend.services.TariffSpecialService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specialtariffs")
@AllArgsConstructor
public class TariffSpecialController {

    private final TariffSpecialService tariffSpecialService;

    @PostMapping("/crear")
    public void crear(TarifaDTO tarifaDTO) {
        tariffSpecialService.saveTariffSpecial(tarifaDTO);
    }

    @PostMapping("/actualizar")
    public void actualizar(TarifaDTO tarifaDTO) {
        tariffSpecialService.updateTariffSpecial(tarifaDTO);
    }

    @PostMapping("/obtenerprice")
    public double obtenerPrecio(PrecioDTO precioDTO) {
        return tariffSpecialService.getPrice(precioDTO.getFechaInicio(), precioDTO.getPrecioRegular());
    }

}

