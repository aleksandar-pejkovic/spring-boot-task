package org.example.utils.dummydata;

import org.example.model.User;

public class UserDummyDataFactory {

    public static final boolean DEFAULT_ACTIVE_STATUS = true;
    private static final String DEFAULT_PASSWORD = "0123456789";

    private UserDummyDataFactory() {
    }

    public static User getUserJohnDoe() {
        return createUser("John", "Doe", "John.Doe");
    }

    public static User getUserJoeJohnson() {
        return createUser("Joe", "Johnson", "Joe.Johnson");
    }

    public static User getUserPeterPeterson() {
        return createUser("Peter", "Peterson", "Peter.Peterson");
    }

    private static User createUser(String firstName, String lastName, String username) {
        return User.builder()
                .isActive(DEFAULT_ACTIVE_STATUS)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password(DEFAULT_PASSWORD)
                .build();
    }
}
