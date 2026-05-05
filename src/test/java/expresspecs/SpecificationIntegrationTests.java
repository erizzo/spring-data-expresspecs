package expresspecs;

import org.springframework.beans.factory.annotation.Autowired;

import expresspecs.example.CustomerRepository;
import lombok.Getter;
import rizzoweb.spring.jpa.BaseJPAIntegrationTest;

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
