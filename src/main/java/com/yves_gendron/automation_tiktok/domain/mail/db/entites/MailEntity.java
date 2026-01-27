package com.yves_gendron.automation_tiktok.domain.mail.db.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;

@Data
@Table(name = "mails")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailEntity {
    @Id
    private String email;
    private String password;
    private String provider;
    @Column(length = 2048)
    private String accessToken;

    private OffsetDateTime usedAt;
    @CreatedDate
    @Column(updatable = false)
    private OffsetDateTime createdAt;
}
