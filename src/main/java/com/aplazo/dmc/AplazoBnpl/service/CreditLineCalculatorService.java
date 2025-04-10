package com.aplazo.dmc.AplazoBnpl.service;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreditLineCalculatorService {

    private final List<AgeRange> ageRanges;

    public CreditLineCalculatorService() {
        this.ageRanges = new ArrayList<>();
        initializeAgeRanges();
    }

    public double calculateCreditLineAmount(int age) {
        return ageRanges.stream()
                .filter(range -> range.isWithin(age))
                .findFirst()
                .map(AgeRange::creditAmount)
                .orElse(0.00);
    }

    private void initializeAgeRanges() {
        ageRanges.add(new AgeRange(18, 25, 3_000.00));
        ageRanges.add(new AgeRange(26, 30, 5_000.00));
        ageRanges.add(new AgeRange(31, 65, 8_000.00));
    }

    private record AgeRange(int minAge, int maxAge, @Getter double creditAmount) {

        public boolean isWithin(int age) {
                return age >= minAge && age <= maxAge;
            }
    }
}
