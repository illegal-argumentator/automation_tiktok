package com.yves_gendron.automation_tiktok.system.client.nst.common.dto;

import lombok.Data;

@Data
public class StartBrowserResponse {

    private int code;

    private ResponseData data;

    private boolean err;

    private String msg;

    @Data
    public static class ResponseData {

        private int port;

        private String profileId;

        private String proxy;

        private String webSocketDebuggerUrl;

    }

}
