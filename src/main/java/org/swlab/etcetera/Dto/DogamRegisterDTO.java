package org.swlab.etcetera.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DogamRegisterDTO {

    private String name;
    private List<RequirementGroup> requirements;
    private CertificateInfo certificate;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RequirementGroup {
        private List<MMOItemRef> alternatives;
        private int amount;
        private boolean consume;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MMOItemRef {
        private String type;
        private String id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CertificateInfo {
        private String type;
        private String id;
        private int amount;
    }
}
