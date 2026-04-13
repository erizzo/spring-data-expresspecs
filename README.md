# JPA Specifications
The `...jpa.specifications` package includes classes to make working with the Spring Data JPA Specifications easier for common use cases.

A typical way to use Spring Data JPA repositories is to define query methods in an interface that extends one of the base Spring repository interfaces.
These can be queries [derived from the method name](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation) 
or [via  `@Query` annotation using JPQL](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.at-query). While convenient
and easy for simple querying scenarios, this technique can become cubersome and verbose when the queries get numerous or need to support multiple and/or optional
parameters. For example, an API to search for `Customer` entities might allow the caller to filter the results by different properties, some of them in nested
child entities. You could easily end up with a derived query method like this:

```java
    Page<Customer> findByAddressZipCodeInOrCreditLimitGreaterThanAndIsActive(List<String> zipCodes, int minCredit, Pageable page);
```
Query methods like this can be very cunbersome and difficult to maintain, espeically as the set of queryable properties grows. Even for a relatively small number
of parameters, these methods can be difficult to grok.

Specifications, on the other hand, offer a composable model of querying that uses JPA's criteria API under the covers. The entry point to using Spring Data
Specifications are methods on the interface `org.springframework.data.jpa.repository.JpaSpecificationExecutor<T>`. The methods in that interface, which can be
included in the super-types of your repository interfaces, all take a `Specification<YourEntity>` to represent the query spec.

Constructing `Specification` objects is extremely flexible but somewhat awkward for common scenarios, which is where this package's helpers come in handy. Let's
use the following example entity model for demonstration. 
> [!NOTE]
> These entities don't necessarily represent the _best_ way to define entities for any production
> system, they've been written to help demonstrate the usefullness of Specifications and this package.

```java
@Entity
@Table(name = "customers")
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

@Entity
@Table(name = "customer_addresses")
public class Address {

    @Id @GeneratedValue
    @EqualsAndHashCode.Include
    private Long id;

    private String street;
    private String city;
    private String state;
    private String zipCode;
    private boolean isPOBox;
}

@Embeddable
public class PhoneNumber {

	private int areaCode;
	private int number;
	private Integer extension;
}

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue
    private Long id;

    @EqualsAndHashCode.Include
    private final UUID orderID = UUID.randomUUID();

    private LocalDate datePlaced;

    @ManyToOne
    @Setter(PACKAGE)
    private Customer customer;


public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
}
```

Using `JPASpecifications` we can define queries such as the following:

> Find customers where the `name` contains any of a set of strings
```java
List<String> searchTerms = List.of("Elmer", "Wile", "Bugs");
Specification<Customer> spec = JPASpecifications.containsAny("name", searchTerms);
List<Customer> results = repo.findAll(spec);
```

> Find customers whose address has a specific zip code
```java
String zipCode = "99762";
Specification<Customer> spec = JPASpecifications.is("address.zipCode", zipCode);
repo.findAll(spec);
``` 
