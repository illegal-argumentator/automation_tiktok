package com.yves_gendron.automation_tiktok.domain.tiktok.service;

import com.yves_gendron.automation_tiktok.common.exception.ApplicationAlreadyInProgressException;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokAccountNotFoundException;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.repository.TikTokRepository;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TikTokService {

    private final TikTokRepository tikTokRepository;

    public TikTokAccount findById(String id) {
        return tikTokRepository.findById(id)
                .orElseThrow(() -> new TikTokAccountNotFoundException("Not found tik tok account by id " + id));
    }

    public List<TikTokAccount> findAll() {
        return tikTokRepository.findAll();
    }

    public List<TikTokAccount> findAllByStatus(Status status) {
        return tikTokRepository.findAllByStatus(status);
    }

    public List<TikTokAccount> saveAllOrThrow(List<TikTokAccount> tikTokAccounts) {
        List<TikTokAccount> tikTokAccountsInProgress = findAllByStatus(Status.IN_PROGRESS);

        if (!tikTokAccountsInProgress.isEmpty()) {
            throw new ApplicationAlreadyInProgressException("Other accounts currently creating");
        }

        return tikTokRepository.saveAllAndFlush(retrieveNotExistingAccounts(tikTokAccounts));
    }

    public void update(String id, UpdateAccountRequest updateAccountRequest) {
        TikTokAccount tikTokAccount = findById(id);

        Optional.ofNullable(updateAccountRequest.getName()).ifPresent(name -> tikTokAccount.getBio().setName(name));
        Optional.ofNullable(updateAccountRequest.getEmail()).ifPresent(tikTokAccount::setEmail);
        Optional.ofNullable(updateAccountRequest.getUsername()).ifPresent(tikTokAccount::setUsername);
        Optional.ofNullable(updateAccountRequest.getPassword()).ifPresent(tikTokAccount::setPassword);
        Optional.ofNullable(updateAccountRequest.getStatus()).ifPresent(tikTokAccount::setStatus);
        Optional.ofNullable(updateAccountRequest.getDob()).ifPresent(dob -> tikTokAccount.getBio().setDob(dob));
        Optional.ofNullable(updateAccountRequest.getCountryCode()).ifPresent(countryCode -> tikTokAccount.getBio().getGeolocation().setCountryCode(countryCode));
        Optional.ofNullable(updateAccountRequest.getRegionName()).ifPresent(regionName -> tikTokAccount.getBio().getGeolocation().setRegionName(regionName));
        Optional.ofNullable(updateAccountRequest.getExecutionMessage()).ifPresent(tikTokAccount::setExecutionMessage);
        Optional.ofNullable(updateAccountRequest.getNstProfileId()).ifPresent(tikTokAccount::setNstProfileId);
        Optional.ofNullable(updateAccountRequest.getProxy()).ifPresent(tikTokAccount::setProxy);
        Optional.ofNullable(updateAccountRequest.getAction()).ifPresent(tikTokAccount::setAction);
        Optional.ofNullable(updateAccountRequest.getLikedPosts()).ifPresent(tikTokAccount::setLikedPosts);
        Optional.ofNullable(updateAccountRequest.getCommentedPosts()).ifPresent(tikTokAccount::setCommentedPosts);
        Optional.ofNullable(updateAccountRequest.getPublishedPosts()).ifPresent(tikTokAccount::setPublishedPosts);
        Optional.ofNullable(updateAccountRequest.getAccountLink()).ifPresent(tikTokAccount::setAccountLink);
        Optional.ofNullable(updateAccountRequest.getAvatarLink()).ifPresent(tikTokAccount::setAvatarLink);

        tikTokRepository.save(tikTokAccount);
    }

    public String generateUsername(String email) {
        return email.substring(0, email.indexOf("@")) + String.valueOf(UUID.randomUUID()).substring(0, 3);
    }

    public void delete(String accountId) {
        TikTokAccount tikTokAccount = findById(accountId);
        tikTokRepository.delete(tikTokAccount);
    }

    private List<TikTokAccount> retrieveNotExistingAccounts(List<TikTokAccount> tikTokAccounts) {
        List<String> emails = tikTokAccounts.stream()
                .map(TikTokAccount::getEmail)
                .filter(Objects::nonNull)
                .toList();

        Set<String> existingEmails = new HashSet<>(tikTokRepository.findAllByEmailIn(emails)
                .stream()
                .map(TikTokAccount::getEmail)
                .toList());

        return tikTokAccounts.stream()
                .filter(acc -> !existingEmails.contains(acc.getEmail()))
                .toList();
    }
}
