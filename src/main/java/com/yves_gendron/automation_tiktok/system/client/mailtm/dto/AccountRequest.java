package com.yves_gendron.automation_tiktok.system.client.mailtm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountRequest {

    private String address;

    private String password;

}
