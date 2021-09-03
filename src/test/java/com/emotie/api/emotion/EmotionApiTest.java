package com.emotie.api.emotion;


import com.emotie.api.AcceptanceTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("EmotionDataLoader")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class EmotionApiTest extends AcceptanceTest {
}
