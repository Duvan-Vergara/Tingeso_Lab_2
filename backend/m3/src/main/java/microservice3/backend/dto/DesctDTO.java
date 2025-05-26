package microservice3.backend.dto;

import lombok.Data;

@Data
public class DesctDTO {
    private Long id;
    private int minveces;
    private int maxveces;
    private double porcentajedesct;
}
