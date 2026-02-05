package com.yves_gendron.automation_tiktok.domain.tiktok.web.dto;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Dob;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Name;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Workflow;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAccountRequest {

    private Name name;

    private String email;

    private String username;

    private String password;

    private Status status;

    private Action action;

    private Integer likedPosts;

    private Integer commentedPosts;

    private Integer publishedPosts;

    private String executionMessage;

    private Dob dob;

    private Proxy proxy;

    private String countryCode;

    private String regionName;

    private String nstProfileId;

    private String accountLink;

    private String avatarLink;

    private Workflow workflow;
}
