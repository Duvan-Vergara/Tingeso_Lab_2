package edu.mtisw.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "tariffs")
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
    private int maxMinutes;

    @Column(nullable = false, name = "regular_price")
    private double regularPrice;

    @Column(nullable = false, name = "total_duration")
    private int totalDuration;

    @Column(nullable = false, name = "weekend_discount_percentage")
    private double weekendDiscountPercentage;

    @Column(nullable = false, name = "holiday_increase_percentage")
    private double holidayIncreasePercentage;

    @Column(nullable = false, name = "weekend_price")
    private double weekendPrice;

    @Column(nullable = false, name = "holiday_price")
    private double holidayPrice;
}

