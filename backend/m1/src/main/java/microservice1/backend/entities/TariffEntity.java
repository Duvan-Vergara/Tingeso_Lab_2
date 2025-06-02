package microservice1.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Entity
@Table(name = "tariffs")
@EnableFeignClients
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, name = "laps")
    private int laps;

    @Column(nullable = false, name = "max_minutes")
    private int max_minutes;

    @Column(nullable = false, name = "regular_price")
    private double regular_price;

    @Column(nullable = false, name = "total_duration")
    private int total_duration;
}