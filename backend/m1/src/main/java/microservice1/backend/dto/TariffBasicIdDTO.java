package microservice1.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TariffBasicIdDTO {
    private long id;
    private int laps;
    private int max_minutes;
}
