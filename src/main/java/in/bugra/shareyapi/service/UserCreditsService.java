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

    public UserCreditsService(UserCreditsRepository userCreditsRepository){
        this.userCreditsRepository = userCreditsRepository;
    }

    public UserCredits createInitialCredits(String clerkId) {
        UserCredits userCredits = new UserCredits();
        userCredits.setClerkId(clerkId);
        userCredits.setCredits(5);
        userCredits.setPlan("BASIC");

        return userCreditsRepository.save(userCredits);
    }
}
