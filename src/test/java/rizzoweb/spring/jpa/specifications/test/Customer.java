package rizzoweb.spring.jpa.specifications.test;

import static lombok.AccessLevel.NONE;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "customers")
@Getter
@Setter
@ToString
@FieldNameConstants
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private Integer creditLimit = 100;

    private OffsetDateTime createdTimestamp;

    @OneToOne
    private Address address;

    @Embedded
    private PhoneNumber phoneNumber;

    @OneToMany(mappedBy = Order.Fields.customer, cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(NONE)
    @Builder.Default
    private Set<Order> orders = new HashSet<>();


    public void addOrder(Order order) {
        orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        if (orders.remove(order)) {
            order.setCustomer(null);
        }
    }
}
