package microservice2.backend.dto;

import lombok.Data;

@Data
public class DesctDTO {
    private Long id;
    private int minpersonas;
    private int maxpersonas;
    private double porcentajedesct;
}
