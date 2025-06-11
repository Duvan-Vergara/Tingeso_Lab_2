package microservice7.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DesctNumberDTO {
    private int minpersonas;
    private int maxpersonas;
    private double porcentajedesct;
}