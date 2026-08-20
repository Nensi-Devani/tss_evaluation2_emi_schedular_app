package emi_schedular.example.tss_evaluation2_emi_schedular_app.strategy.impl;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class CalculateEmi {

    private static final int SCALE = 2;
    private static final MathContext MC = new MathContext(20);

    /*
     * Standard EMI formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1).
     * Falls back to a simple equal split when the rate is zero.
     */
    public static BigDecimal estimateEmiCalculate(BigDecimal principal, int tenureMonths, BigDecimal monthlyRate) {
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(tenureMonths), SCALE, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal factor = onePlusR.pow(tenureMonths, MC);
        BigDecimal numerator = principal.multiply(monthlyRate, MC).multiply(factor, MC);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE, MC);
        return numerator.divide(denominator, MC).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
