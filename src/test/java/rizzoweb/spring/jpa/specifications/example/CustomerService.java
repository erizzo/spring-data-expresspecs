package rizzoweb.spring.jpa.specifications.example;

import static rizzoweb.spring.jpa.specifications.example.CustomerSpecifications.isActive;
import static rizzoweb.spring.jpa.specifications.example.CustomerSpecifications.nameContainsIgnoreCase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * Service for managing {@link Customer} entities.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository repository;

    /**
     * Finds active customers whose name contains the given partial name, ignoring case.
     *
     * @param partialName the partial name to search for
     * @return a list of matching active customers
     */
    public List<Customer> findActiveCustomersByName(String partialName) {
        return repository.findAll(nameContainsIgnoreCase(partialName).and(isActive()));
    }
}
