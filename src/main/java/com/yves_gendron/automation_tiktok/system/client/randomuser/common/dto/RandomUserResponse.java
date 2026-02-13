package com.yves_gendron.automation_tiktok.system.client.randomuser.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Dob;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Name;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomUserResponse {

    private List<RandomResult> results;

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RandomResult {

        private String gender;

        @JsonIgnoreProperties(ignoreUnknown = true)
        private Name name;

        private String email;

        private Login login;

        private Dob dob;

        private Picture picture;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Login {

            private String username;

        }

        @Data
        @Builder
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Picture {

            private String large;

        }

    }
}
