package microservice4.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "special_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecialDayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @Column(nullable = false, name = "description")
    private String description;

}
