package org.swlab.etcetera.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TutorialDTO {

    private String uuid;
    private boolean tutorialCompleted;

}
