package com.yves_gendron.automation_tiktok.system.client.mailtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetMessagesResponse {

    @JsonProperty("hydra:totalItems")
    private int totalItems;

    @JsonProperty("hydra:member")
    private List<Message> messages;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {

        private String id;

        private String subject;

    }

}
