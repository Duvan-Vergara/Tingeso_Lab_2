package microservice5.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "reserves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(nullable = false, name = "reserveday", columnDefinition = "DATE")
    private LocalDate reserveday;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false, name = "begin")
    private LocalTime begin;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(nullable = false, name = "finish")
    private LocalTime finish;

    @ManyToMany
    @JoinTable(
            name = "reserves_users",
            joinColumns = @JoinColumn(name = "reserve_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserEntity> reserves_users;

    @Column(nullable = false, name = "tariff_id")
    private Long tariff_id;

    @Column(nullable = false, name = "final_price")
    private double final_price;
}
