package microservice4.backend.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class TarifaDTO {
    private double weekendDiscountPercentage;
    private double holidayIncreasePercentage;
}
