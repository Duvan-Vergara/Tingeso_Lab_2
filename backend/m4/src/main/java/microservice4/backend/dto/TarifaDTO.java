package microservice4.backend.dto;

import lombok.Data;

@Data
public class TarifaDTO {
    private double weekendDiscountPercentage;
    private double holidayIncreasePercentage;
}
