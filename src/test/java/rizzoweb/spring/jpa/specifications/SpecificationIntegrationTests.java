package rizzoweb.spring.jpa.specifications;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;
import rizzoweb.spring.jpa.specifications.example.CustomerRepository;

public abstract class SpecificationIntegrationTests extends BaseJPAIntegrationTest implements
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
