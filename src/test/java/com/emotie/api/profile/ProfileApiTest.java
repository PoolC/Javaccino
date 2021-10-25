package com.emotie.api.profile;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("ProfileDataLoader")
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class ProfileApiTest {
}
