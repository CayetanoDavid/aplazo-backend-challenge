package com.aplazo.dmc.AplazoBnpl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreditLineCalculatorServiceTest {

    private CreditLineCalculatorService creditLineCalculatorService;

    @BeforeEach
    void setUp() {
        creditLineCalculatorService = new CreditLineCalculatorService();
    }

    @Test
    void calculateCreditLineAmount_AgeInFirstRange_ShouldReturn3000() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(20);
        assertEquals(3000.00, credit);
    }

    @Test
    void calculateCreditLineAmount_AgeInSecondRange_ShouldReturn5000() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(28);
        assertEquals(5000.00, credit);
    }

    @Test
    void calculateCreditLineAmount_AgeInThirdRange_ShouldReturn8000() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(40);
        assertEquals(8000.00, credit);
    }

    @Test
    void calculateCreditLineAmount_AgeBelowRange_ShouldReturn0() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(17);
        assertEquals(0.00, credit);
    }

    @Test
    void calculateCreditLineAmount_AgeAboveRange_ShouldReturn0() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(70);
        assertEquals(0.00, credit);
    }

    @Test
    void calculateCreditLineAmount_ExactLowerBound_ShouldReturnCorrectAmount() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(18);
        assertEquals(3000.00, credit);
    }

    @Test
    void calculateCreditLineAmount_ExactUpperBound_ShouldReturnCorrectAmount() {
        double credit = creditLineCalculatorService.calculateCreditLineAmount(65);
        assertEquals(8000.00, credit);
    }
}
