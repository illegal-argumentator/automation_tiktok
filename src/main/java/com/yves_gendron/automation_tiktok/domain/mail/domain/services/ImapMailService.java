package com.yves_gendron.automation_tiktok.domain.mail.domain.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ImapMailService implements MailService {

    private final Store imapStore;

    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000;
    private static final int MESSAGE_SEARCH_LIMIT = 100;

    @Override
    public String getEmail() {
        return "";
    }

    public String retrieveCodeFromMessage(String email, OffsetDateTime date) {
        return verifyUserMail(email);
    }

    public String verifyUserMail(String email) {
        try {
            Folder inbox = imapStore.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            String code = "";

            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {

                inbox.getMessageCount();

                Message[] messages = getLatestMessages(inbox, MESSAGE_SEARCH_LIMIT);

                for (int i = messages.length - 1; i >= 0; i--) {
                    Message message = messages[i];

                    if (!isMessageToUser(message, email)) continue;

                    String extracted = extractCodeFromMessage(message);
                    if (extracted != null) {
                        code = extracted;
                        break;
                    }
                }

                if (!code.isEmpty()) break;

                if (attempt < MAX_RETRIES) {
                    Thread.sleep(RETRY_DELAY_MS);
                }
            }

            return code;
        } catch (MessagingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Message[] getLatestMessages(Folder folder, int limit) throws MessagingException {
        int total = folder.getMessageCount();
        if (total == 0) return new Message[0];

        int start = Math.max(1, total - limit + 1);
        return folder.getMessages(start, total);
    }

    private boolean isMessageToUser(Message message, String email) {
        try {
            Address[] recipients = message.getRecipients(Message.RecipientType.TO);
            if (recipients == null) return false;

            for (Address addr : recipients) {
                if (addr instanceof InternetAddress ia) {
                    if (ia.getAddress().equalsIgnoreCase(email)) return true;
                }
            }
            return false;

        } catch (Exception e) {
            return false;
        }
    }

    private String extractCodeFromMessage(Message message) {
        try {
            String text = extractTextRecursive(message);
            return findCode(text);

        } catch (Exception e) {
            return null;
        }
    }

    private String extractTextRecursive(Part part) throws Exception {
        if (part.isMimeType("text/plain")) {
            return (String) part.getContent();
        }

        if (part.isMimeType("text/html")) {
            String html = (String) part.getContent();
            return html.replaceAll("<[^>]+>", "").replaceAll("&nbsp;", " ").trim();
        }

        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < mp.getCount(); i++) {
                String sub = extractTextRecursive(mp.getBodyPart(i));
                if (sub != null) sb.append(sub).append("\n");
            }

            return sb.toString();
        }

        return null;
    }

    private String findCode(String text) {
        if (text == null) return null;

        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group() : null;
    }

}
