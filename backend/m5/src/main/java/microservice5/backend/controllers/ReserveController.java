package microservice5.backend.controllers;


import lombok.RequiredArgsConstructor;
import microservice5.backend.dto.MADDTO;
import microservice5.backend.dto.ReserveBasicDTO;
import microservice5.backend.dto.ReserveDTO;
import microservice5.backend.dto.StarEndDTO;
import microservice5.backend.entities.ReserveEntity;
import microservice5.backend.services.ReserveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reserves")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    @GetMapping("/")
    public ResponseEntity<List<ReserveEntity>> listReservers() {
        List<ReserveEntity> reserves = reserveService.getReserves();
        return ResponseEntity.ok(reserves);
    }

    @PostMapping("/getAll")
    public List<ReserveBasicDTO> getAll(@RequestBody StarEndDTO tiempo) {
        return reserveService.getReservesByDateBetween(tiempo.getStar(), tiempo.getEnd());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReserveEntity> getReserveById(@PathVariable Long id) {
        ReserveEntity reserve = reserveService.getReserveById(id);
        return ResponseEntity.ok(reserve);
    }

    @PostMapping("/")
    public ResponseEntity<ReserveEntity> saveReserves(@RequestBody ReserveEntity reserve) {
        ReserveEntity newReserve = reserveService.saveReserve(reserve);
        return ResponseEntity.ok(newReserve);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteReserveById(@PathVariable Long id) throws Exception {
        reserveService.deleteReserveById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/week")
    public ResponseEntity<List<ReserveDTO>> listReservesByWeek(@RequestBody MADDTO maddto) {
        List<ReserveDTO> reserves = reserveService.getReserveByWeek(maddto.getAnio(), maddto.getMes(), maddto.getDia());
        return ResponseEntity.ok(reserves);
    }

    @GetMapping("/{id}/payment-receipt")
    public ResponseEntity<?> sendPaymentReceipt(@PathVariable Long id) {
        try {
            ReserveEntity reserve = reserveService.getReserveById(id);
            reserveService.sendPaymentReceipts(reserve);
            return ResponseEntity.ok().body("Comprobante de pago enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el comprobante de pago: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/payment-receipt-v2")
    public ResponseEntity<?> sendPaymentReceiptV2(@PathVariable Long id) {
        try {
            ReserveEntity reserve = reserveService.getReserveById(id);
            reserveService.sendPaymentReceipts_2(reserve);
            return ResponseEntity.ok().body("Comprobante de pago enviado correctamente (versión 2)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar el comprobante de pago (versión 2): " + e.getMessage());
        }
    }

    @PostMapping("/calculate-price")
    public ResponseEntity<Double> calculatePrice(@RequestBody ReserveEntity reserve) {
        if (reserve.getTariff_id() == null || reserve.getReserves_users() == null || reserve.getReserves_users().isEmpty()) {
            return ResponseEntity.badRequest().body(0.0);
        }
        double finalPrice = reserveService.calculateFinalPrice(reserve, LocalDate.now().getMonthValue());
        return ResponseEntity.ok(finalPrice);
    }
}
