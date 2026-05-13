package expresspecs;

import org.springframework.beans.factory.annotation.Autowired;

import expresspecs.datetime.DateTimeSpecificationIntegrationTests;
import expresspecs.datetime.OnDateTypeCoverageTests;
import expresspecs.example.Customer;
import expresspecs.example.CustomerRepository;
import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

public abstract class SpecificationIntegrationTests extends BaseJPAIntegrationTest<Customer> implements
		StringSpecificationIntegrationTests,
		BasicSpecificationIntegrationTests,
		RangeSpecificationIntegrationTests,
		DateTimeSpecificationIntegrationTests,
		OnDateTypeCoverageTests,
		CollectionSpecificationIntegrationTests,
		SpecificationExtensionsIntegrationTests {

	@Autowired
	@Getter
	protected CustomerRepository repo;

}
