package com.tinyquest.hub;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModulithStructureTest {
    @Test
    void verifyModularStructure() {
        // 메인 @SpringBootApplication 클래스 기준
        var modules = ApplicationModules.of(HubApplication.class);
        modules.verify(); // 규칙 위반 시 테스트 실패
    }
}
