package in.bugra.shareyapi.repository;

import in.bugra.shareyapi.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, String > {
    Optional<Profile> findByEmail(String email);
    Boolean existsByEmail(String email);
    Profile findByClerkId(String clerkId);
    Boolean existsByClerkId(String clerkId);
}