package expresspecs;

import org.springframework.beans.factory.annotation.Autowired;

import expresspecs.example.Customer;
import expresspecs.example.CustomerRepository;
import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

public abstract class SpecificationIntegrationTests extends BaseJPAIntegrationTest<Customer> implements
		StringSpecificationIntegrationTests,
		BasicSpecificationIntegrationTests,
		RangeSpecificationIntegrationTests,
		DateTimeSpecificationIntegrationTests,
		CollectionSpecificationIntegrationTests,
		SpecificationExtensionsIntegrationTests {

	@Autowired
	@Getter
	protected CustomerRepository repo;

}
