package expresspecs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import expresspecs.datetime.DateTimeSpecificationIntegrationTests;
import expresspecs.datetime.OnDateTypeCoverageTests;
import expresspecs.datetime.Socialite;
import expresspecs.datetime.SocialiteRepository;
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

	@Autowired
	protected SocialiteRepository socialiteRepository;

	@Override
	public JpaSpecificationExecutor<Socialite> getSocialiteRepo() {
		return socialiteRepository;
	}

}
