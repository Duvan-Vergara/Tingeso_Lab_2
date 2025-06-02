package microservice5.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MADDTO {
    private int año;
    private int mes;
    private int dia;
}
