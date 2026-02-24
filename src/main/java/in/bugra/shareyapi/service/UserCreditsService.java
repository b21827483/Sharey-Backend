package in.bugra.shareyapi.service;

import in.bugra.shareyapi.entity.UserCredits;
import in.bugra.shareyapi.repository.UserCreditsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCreditsService {

    @Autowired
    private final UserCreditsRepository userCreditsRepository;
    private final ProfileService profileService;

    public UserCreditsService(UserCreditsRepository userCreditsRepository,
                              ProfileService profileService){
        this.userCreditsRepository = userCreditsRepository;
        this.profileService = profileService;
    }

    public UserCredits createInitialCredits(String clerkId) {
        UserCredits userCredits = new UserCredits();
        userCredits.setClerkId(clerkId);
        userCredits.setCredits(5);
        userCredits.setPlan("BASIC");

        return userCreditsRepository.save(userCredits);
    }

    public UserCredits getUserCredits(String clerkId) {
        return userCreditsRepository.findByClerkId(clerkId)
                        .orElseGet(() -> createInitialCredits(clerkId));
    }

    public Boolean hasEnoughCredits(int requiredCredits, String clerkId) {
        UserCredits userCredits = getUserCredits(clerkId);
        return userCredits.getCredits() >= requiredCredits;
    }

    public UserCredits consumeCredit(String clerkId) {
        UserCredits userCredits = getUserCredits(clerkId);
        if (userCredits.getCredits() <= 0) {
            return null;
        }

        userCredits.setCredits(userCredits.getCredits() - 1);
        return userCreditsRepository.save(userCredits);

    }
}
