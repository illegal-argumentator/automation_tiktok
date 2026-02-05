package com.yves_gendron.automation_tiktok.domain.tiktok.model;

import com.yves_gendron.automation_tiktok.common.dto.BaseAccount;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Workflow;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TikTokAccount extends BaseAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Action action;

    private String avatarLink;

    private String accountLink;

    private int likedPosts;

    @Embedded
    private Workflow workflow;

    @ManyToOne
    @JoinColumn(name = "proxy_id")
    private Proxy proxy;

    private int commentedPosts;

    private int publishedPosts;
}
