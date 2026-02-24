package in.bugra.shareyapi.service;

import in.bugra.shareyapi.dto.ProfileDTO;
import in.bugra.shareyapi.entity.Profile;
import in.bugra.shareyapi.exception.EmailAlreadyExists;
import in.bugra.shareyapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProfileService {

    @Autowired
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileDTO createProfile(ProfileDTO profileDTO) {

        if (profileRepository.existsByEmail(profileDTO.getEmail())) {
            throw new EmailAlreadyExists("Email already exists: " + profileDTO.getEmail());
        }

        Profile profile = new Profile();
        profile.setClerkId(profileDTO.getClerkId());
        profile.setEmail(profileDTO.getEmail());
        profile.setFirstName(profileDTO.getFirstName());
        profile.setLastName(profileDTO.getLastName());
        profile.setAvatarUrl(profileDTO.getAvatarUrl());
        profile.setCredits(5);
        profile.setCreatedAt(Instant.now());

        profile = profileRepository.save(profile);

        profileDTO.setId(profile.getId());
        profileDTO.setClerkId(profile.getClerkId());
        profileDTO.setFirstName(profile.getFirstName());
        profileDTO.setLastName(profile.getLastName());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setAvatarUrl(profile.getAvatarUrl());
        profileDTO.setCredits(profile.getCredits());
        profileDTO.setCreatedAt(profile.getCreatedAt());

        return profileDTO;
    }

    public ProfileDTO updateProfile(ProfileDTO profileDTO) {
        Profile existingProfile = profileRepository.findByClerkId(profileDTO.getClerkId());

        if (existingProfile != null) {
            if (profileDTO.getEmail() != null && !profileDTO.getEmail().isEmpty()) {
                existingProfile.setEmail(profileDTO.getEmail());
            }

            if(profileDTO.getFirstName() != null && !profileDTO.getFirstName().isEmpty()) {
                existingProfile.setFirstName(profileDTO.getFirstName());
            }

            if(profileDTO.getLastName() != null && !profileDTO.getLastName().isEmpty()) {
                existingProfile.setLastName(profileDTO.getLastName());
            }

            if(profileDTO.getAvatarUrl() != null && !profileDTO.getAvatarUrl().isEmpty()) {
                existingProfile.setAvatarUrl(profileDTO.getAvatarUrl());
            }

            existingProfile = profileRepository.save(existingProfile);

            ProfileDTO profileDTOUpdated = new ProfileDTO();
            profileDTOUpdated.setId(existingProfile.getId());
            profileDTOUpdated.setClerkId(existingProfile.getClerkId());
            profileDTOUpdated.setFirstName(existingProfile.getFirstName());
            profileDTOUpdated.setLastName(existingProfile.getLastName());
            profileDTOUpdated.setEmail(existingProfile.getEmail());
            profileDTOUpdated.setAvatarUrl(existingProfile.getAvatarUrl());
            profileDTOUpdated.setCredits(existingProfile.getCredits());
            profileDTOUpdated.setCreatedAt(existingProfile.getCreatedAt());

            return profileDTOUpdated;
        }
        return null;
    }

    public boolean existsByClerkId(String clerkId) {
        return profileRepository.existsByClerkId(clerkId);
    }

    public void deleteProfile(String clerkId) {
        Profile existingProfile = profileRepository.findByClerkId(clerkId);

        if (existingProfile != null) {
            profileRepository.delete(existingProfile);
        }
    }

    public Profile getCurrentProfile() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("User not authenticated");
        }

        String clerkId = SecurityContextHolder.getContext().getAuthentication().getName();
        return profileRepository.findByClerkId(clerkId);
    }
}
