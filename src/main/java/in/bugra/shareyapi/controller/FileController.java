package in.bugra.shareyapi.controller;

import in.bugra.shareyapi.dto.FileMetadataDTO;
import in.bugra.shareyapi.entity.UserCredits;
import in.bugra.shareyapi.service.FileMetadataService;
import in.bugra.shareyapi.service.UserCreditsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileMetadataService fileMetadataService;
    private final UserCreditsService userCreditsService;

    public FileController(FileMetadataService fileMetadataService,
                          UserCreditsService userCreditsService) {
        this.fileMetadataService = fileMetadataService;
        this.userCreditsService = userCreditsService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFiles(@RequestPart("files")MultipartFile[] files, @AuthenticationPrincipal String clerkId) throws IOException {
        System.out.println("🎯 CONTROLLER METHOD CALLED!"); // ← Add this
        System.out.println("📤 Upload from: " + clerkId);
        System.out.println("📄 File: " + files[0].getOriginalFilename());
        Map<String, Object> response = new HashMap<>();
        List<FileMetadataDTO> list = fileMetadataService.uploadFiles(files, clerkId);

        UserCredits finalCredits = userCreditsService.getUserCredits(clerkId);

        response.put("files", list);
        response.put("remainingCredits", finalCredits.getCredits());
        return ResponseEntity.ok(response);
    }
}
