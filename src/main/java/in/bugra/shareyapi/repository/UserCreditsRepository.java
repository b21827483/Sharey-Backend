package in.bugra.shareyapi.repository;

import in.bugra.shareyapi.entity.UserCredits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCreditsRepository extends JpaRepository<UserCredits, String> {
    Optional<UserCredits> findByClerkId(String clerkId);
}
