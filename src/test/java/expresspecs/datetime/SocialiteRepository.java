package expresspecs.datetime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SocialiteRepository extends JpaRepository<Socialite, Long>, JpaSpecificationExecutor<Socialite> {
}
