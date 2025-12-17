package com.yves_gendron.automation_tiktok.system.client.nst.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateProfileRequest {
    private String name;
    private String platform;
    private String proxy;
    private Fingerprint fingerprint;
    private List<String> startupUrls;
    private Map<String, Object> args;

    @Data
    public static class Fingerprint {
        private Geolocation geolocation;
        private Localization localization;
        private Screen screen;
        private WebRTC webrtc;
        private Map<String, String> flags;

        @Data
        public static class WebRTC {
            private String publicIp;
        }

        @Data
        public static class Screen {
            private Integer width;
            private Integer height;
        }

        @Data
        public static class Geolocation {
            private String latitude;
            private String longitude;
            private String accuracy;
        }

        @Data
        public static class Localization {
            private String language;
            private List<String> languages;
            private String timezone;
        }
    }
}


