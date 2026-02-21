package in.bugra.shareyapi.controller;

import in.bugra.shareyapi.dto.ProfileDTO;
import in.bugra.shareyapi.service.ProfileService;
import in.bugra.shareyapi.service.UserCreditsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhooks")
public class ClerkWebhookController {

    @Value("${clerk.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private final ProfileService profileService;
    @Autowired
    private final UserCreditsService userCreditsService;

    public ClerkWebhookController(ProfileService profileService, UserCreditsService userCreditsService) {
        this.profileService = profileService;
        this.userCreditsService = userCreditsService;
    }

    @PostMapping("/clerk")
    public ResponseEntity<?> handleClerkWebhook(
            @RequestHeader("svix-id") String svixId,
            @RequestHeader("svix-timestamp") String svixTimestamp,
            @RequestHeader("svix-signature") String svixSignature,
            @RequestBody String payload) {
        try {
            boolean isValid = verifyWebhookSignature(svixId, svixTimestamp, svixSignature, payload);
            if (!isValid){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid webhook signature");
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(payload);

            String eventType = rootNode.path("type").asText();

            switch (eventType) {
                case "user.created":
                    handleUserCreated(rootNode.path("data"));
                    break;
                case "user.updated":
                    handleUserUpdated(rootNode.path("data"));
                    break;
                case "user.deleted":
                    handleUserDeleted(rootNode.path("data"));
                    break;
            }
            return ResponseEntity.ok("Webhook processed successfully");

        } catch (Exception e) {
            System.err.println("❌ Exception in webhook handler: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    private void handleUserDeleted(JsonNode data) {
        String clerkId = data.path("id").asText();
        profileService.deleteProfile(clerkId);
    }

    private void handleUserCreated(JsonNode data) {
        String clerkId = data.path("id").asText();

        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");
        if (emailAddresses.isArray() && emailAddresses.size() > 0) {
            email = emailAddresses.get(0).path("email_address").asText();
        }

        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String avatarUrl = data.path("image_url").asText("");

        ProfileDTO newProfile = new ProfileDTO();
        newProfile.setClerkId(clerkId);
        newProfile.setEmail(email);
        newProfile.setFirstName(firstName);
        newProfile.setLastName(lastName);
        newProfile.setAvatarUrl(avatarUrl);

        profileService.createProfile(newProfile);
        userCreditsService.createInitialCredits(clerkId);

        System.out.println("✅ Created profile for user: " + clerkId);
    }

    private void handleUserUpdated(JsonNode data) {
        String clerkId = data.path("id").asText();

        String email = "";
        JsonNode emailAddresses = data.path("email_addresses");
        if(emailAddresses.isArray() && emailAddresses.size() > 0) {
            email = emailAddresses.get(0).path("email_address").asText();
        }

        String firstName = data.path("first_name").asText("");
        String lastName = data.path("last_name").asText("");
        String avatarUrl = data.path("image_url").asText("");

        ProfileDTO updatedProfile = new ProfileDTO();
        updatedProfile.setClerkId(clerkId);
        updatedProfile.setEmail(email);
        updatedProfile.setFirstName(firstName);
        updatedProfile.setLastName(lastName);
        updatedProfile.setAvatarUrl(avatarUrl);

        updatedProfile = profileService.updateProfile(updatedProfile);

        if (updatedProfile == null) {
            handleUserCreated(data);
        }
    }

    private boolean verifyWebhookSignature(String svixId, String svixTimestamp, String svixSignature, String payload) {
        return true;
    }
}
