package microservice7.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveBasicDTO {
    private LocalDate reserveday;
    private long tariffId;
    private double finalPrice;
    private int groupSize;
}