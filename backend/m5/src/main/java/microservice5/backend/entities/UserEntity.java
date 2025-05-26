package microservice5.backend.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, name = "rut")
    private String rut;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "lastname")
    private String lastName;

    @Column(nullable = false, name = "email")
    private String email;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(nullable = false, name = "birthdate")
    private LocalDate birthDate;
}
