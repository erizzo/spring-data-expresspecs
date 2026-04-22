package rizzoweb.spring.jpa.specifications.example;

import static lombok.AccessLevel.PACKAGE;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Entity
@Table(name = "orders")
@Getter
@Setter
@ToString(exclude = "customer")	// to avoid circular reference in toString()
@FieldNameConstants
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    @Id @GeneratedValue
    private Long id;

    @EqualsAndHashCode.Include
    private final UUID orderID = UUID.randomUUID();

    private LocalDate datePlaced;

    @ManyToOne
    @Setter(PACKAGE)
    private Customer customer;
}
