package org.example.utils.credentials;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CredentialsGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private static final int LENGTH = 10;

    private final UserRepository userRepository;

    @Autowired
    public CredentialsGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateRandomPassword() {
        log.info("Generating password...");
        String password = RandomStringUtils.random(10, CHARACTERS);

        String regex = "^[A-Za-z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        if (matcher.matches()) {
            return password;
        } else {
            return generateRandomPassword();
        }
    }

    @Transactional(readOnly = true)
    public String generateUsername(User user) {
        log.info("Generating username...");
        String baseUsername = user.getFirstName() + "." + user.getLastName();
        long count = userRepository.countByUsernameContaining(baseUsername);
        if (count > 0) {
            long usernameSuffix = count + 1;
            return baseUsername + usernameSuffix;
        }
        return baseUsername;
    }
}
