package com.yves_gendron.automation_tiktok.system.client.nst.common.dto.builder;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.system.client.nst.common.dto.CreateProfileRequest;

import java.util.List;

public class CreateProfileRequestBuilder {

    public static CreateProfileRequest buildCreateProfileRequest(String profileName, Proxy proxy, String groupId) {
        CreateProfileRequest request = new CreateProfileRequest();
        request.setName(profileName);
        request.setPlatform("Windows");
        request.setProxy(
                "http://%s:%s@%s:%d".formatted(
                        proxy.getUsername(),
                        proxy.getPassword(),
                        proxy.getHost(),
                        proxy.getPort()
                )
        );
        request.setGroupId(groupId);

        CreateProfileRequest.Fingerprint fingerprint = new CreateProfileRequest.Fingerprint();

        CreateProfileRequest.Fingerprint.Localization localization = new CreateProfileRequest.Fingerprint.Localization();
        localization.setLanguage("en-US");
        localization.setLanguages(List.of("en-US", "en"));
        fingerprint.setLocalization(localization);

        request.setFingerprint(fingerprint);

        return request;
    }
}
