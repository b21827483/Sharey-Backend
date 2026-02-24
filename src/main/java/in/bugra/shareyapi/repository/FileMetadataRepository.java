package in.bugra.shareyapi.repository;

import in.bugra.shareyapi.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, String> {
    List<FileMetadata> findByClerkId(String clerkId);

    Long countByClerkId(String clerkId);
}
