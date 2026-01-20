package com.yves_gendron.automation_tiktok.domain.mail.db.repositories;

import com.yves_gendron.automation_tiktok.domain.mail.db.entites.MailEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Set;

public interface MailRepository extends ListCrudRepository<MailEntity,String>,
        JpaSpecificationExecutor<MailEntity> {

    @Query("SELECT m.email FROM MailEntity m")
    Set<String> getEmails();
}
