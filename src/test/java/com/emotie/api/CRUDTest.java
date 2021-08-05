package com.emotie.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class CRUDTest {

    public abstract void 작성_테스트();

    public abstract void 조회_테스트();

    public abstract void 갱신_테스트();

    public abstract void 삭제_테스트();
}
