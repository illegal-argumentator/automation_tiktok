package com.yves_gendron.automation_tiktok.domain.tiktok.repository;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TikTokRepository extends JpaRepository<TikTokAccount, String> {

    List<TikTokAccount> findAllByStatus(Status status);

    List<TikTokAccount> findAllByAction(Action action);

    void deleteAllByStatus(Status status);

    List<TikTokAccount> findAllByEmailIn(Collection<String> emails);
}
