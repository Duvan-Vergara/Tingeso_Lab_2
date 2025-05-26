package microservice3.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "desctfrecu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesctFrecuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    private int minveces;
    private int maxveces;
    private double porcentajedesct;
}