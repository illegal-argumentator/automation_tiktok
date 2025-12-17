package com.yves_gendron.automation_tiktok.common.dto;

import com.yves_gendron.automation_tiktok.common.dto.embedded.Bio;
import com.yves_gendron.automation_tiktok.common.type.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAccount extends AuditingEntity {

    @Column(unique = true)
    private String email;

    private String password;

    @Embedded
    private Bio bio;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String nstProfileId;

    private String executionMessage;

}
