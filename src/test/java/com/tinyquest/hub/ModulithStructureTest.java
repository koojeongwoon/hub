package com.tinyquest.hub;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.JavaPackage;

class ModulithStructureTest {

    @Test
    @DisplayName("모듈 구조 규칙을 검증한다")
    void verifyModularStructure() {
        var modules = ApplicationModules.of(HubApplication.class);
        modules.verify();
    }

    @Test
    @DisplayName("행사/프로모션/알림 모듈이 모듈 경계에 포함된다")
    void containsEventPromotionNotificationModules() {
        var modules = ApplicationModules.of(HubApplication.class);
        Set<String> basePackages = modules.stream()
                .map(ApplicationModule::getBasePackage)
                .map(JavaPackage::getName)
                .collect(Collectors.toSet());

        assertThat(basePackages)
                .contains(
                        "com.tinyquest.hub.event",
                        "com.tinyquest.hub.promotion",
                        "com.tinyquest.hub.notification"
                );
    }
}
