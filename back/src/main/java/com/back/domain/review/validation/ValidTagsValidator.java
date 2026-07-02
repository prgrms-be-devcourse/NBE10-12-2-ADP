package com.back.domain.review.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidTagsValidator implements ConstraintValidator<ValidTags, List<String>> {
    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return isMaxSize5() && isAllValidTag

        Map<String, Integer> tagCounts = new HashMap<>();

        boolean isMaxSize5 = value.size() <= 5;
        boolean isAllTagValid = true;
        boolean isAllTagUnique = true;

        for (String tag : value) {
            boolean isValid = 1 <= tag.length() && tag.length() <= 20;

            tagCounts.put(tag, Optional.ofNullable(tagCounts.get(tag)).orElse(0) + 1);
        }
        for (int count : tagCounts.values()) {
            isAllTagUnique = count <= 1;
        }

        tagCounts.forEach((_, count) -> {
            if (count > 1) isAllTagUnique = true;
        });

        return isMaxSize5 && isAllTagValid;
    }
}