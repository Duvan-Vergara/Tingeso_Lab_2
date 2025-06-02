package microservice4.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tariff_special")
public class TariffSpecial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, name = "weekend_discount_percentage")
    private double weekendDiscountPercentage;

    @Column(nullable = false, name = "holiday_increase_percentage")
    private double holidayIncreasePercentage;
}
