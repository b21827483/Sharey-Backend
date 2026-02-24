package in.bugra.shareyapi.service;

import in.bugra.shareyapi.dto.FileMetadataDTO;
import in.bugra.shareyapi.entity.FileMetadata;
import in.bugra.shareyapi.entity.Profile;
import in.bugra.shareyapi.repository.FileMetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileMetadataService {

    private final ProfileService profileService;
    private final UserCreditsService userCreditsService;
    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadataService(ProfileService profileService,
                               UserCreditsService userCreditsService,
                               FileMetadataRepository fileMetadataRepository) {
        this.profileService = profileService;
        this.userCreditsService = userCreditsService;
        this.fileMetadataRepository = fileMetadataRepository;

    }

    public List<FileMetadataDTO> uploadFiles(MultipartFile[] files, String clerkId) throws IOException {
        Profile currentProfile = profileService.getCurrentProfile();

        if (!userCreditsService.hasEnoughCredits(files.length, clerkId)) {
            throw new RuntimeException("You don't have enough credits to upload" + files.length + "files.");
        }

        List<FileMetadata> savedFiles = new ArrayList<>();
        Path uploadPath = Paths.get("upload").toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        for (MultipartFile file: files) {
            String fileName = UUID.randomUUID()+"."+ StringUtils.getFilename(file.getOriginalFilename());
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setFileLocation(targetLocation.toString());
            fileMetadata.setName(file.getOriginalFilename());
            fileMetadata.setSize(file.getSize());
            fileMetadata.setType(file.getContentType());
            fileMetadata.setClerkId(currentProfile.getClerkId());
            fileMetadata.setIsPublic(false);
            fileMetadata.setUploadedAt(LocalDateTime.now());

            userCreditsService.consumeCredit(clerkId);
            savedFiles.add(fileMetadataRepository.save(fileMetadata));
        }

        return savedFiles.stream().map(fileMetadata -> mapToDTO(fileMetadata))
                .collect(Collectors.toList());
    }

    private FileMetadataDTO mapToDTO(FileMetadata fileMetadata) {
        FileMetadataDTO fileMetadataDTO = new FileMetadataDTO();
        fileMetadataDTO.setId(fileMetadata.getId());
        fileMetadataDTO.setFileLocation(fileMetadata.getFileLocation());
        fileMetadataDTO.setName(fileMetadata.getName());
        fileMetadataDTO.setSize(fileMetadata.getSize());
        fileMetadataDTO.setType(fileMetadata.getType());
        fileMetadataDTO.setClerkId(fileMetadata.getClerkId());
        fileMetadataDTO.setIsPublic(false);
        fileMetadataDTO.setUploadedAt(fileMetadata.getUploadedAt());

        return fileMetadataDTO;
    }
}
