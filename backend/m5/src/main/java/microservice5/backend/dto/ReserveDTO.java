package microservice5.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveDTO {

    private LocalDate reserveday;

    private LocalTime begin;

    private LocalTime finish;

    private String username;
}