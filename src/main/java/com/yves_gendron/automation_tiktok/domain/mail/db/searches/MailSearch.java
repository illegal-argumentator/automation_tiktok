package com.yves_gendron.automation_tiktok.domain.mail.db.searches;

import com.yves_gendron.automation_tiktok.domain.mail.db.entites.MailEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSearch implements Specification<MailEntity> {
    @Singular(ignoreNullCollections = true)
    private Collection<String> emails;
    private Boolean isUsed;

    @Override
    public @Nullable Predicate toPredicate(Root<MailEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        ArrayList<Predicate> predicates = new ArrayList<>();

        if (!CollectionUtils.isEmpty(emails)) {
            predicates.add(root.get("email").in(emails));
        }

        if (Boolean.TRUE.equals(isUsed)) {
            predicates.add(cb.isNotNull(root.get("usedAt")));
        }else if (Boolean.FALSE.equals(isUsed)) {
            predicates.add(cb.isNull(root.get("usedAt")));
        }
        return predicates.isEmpty() ? null : cb.and(predicates.toArray(Predicate[]::new));
    }
}
