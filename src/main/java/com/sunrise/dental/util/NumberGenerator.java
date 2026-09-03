package com.sunrise.dental.util;

import java.time.Year;

public final class NumberGenerator {

    private NumberGenerator() {
    }

    public static String generatePatientNumber(int sequence) {
        return formatNumber("PAT", sequence);
    }

    public static String generateAppointmentNumber(int sequence) {
        return formatNumber("APT", sequence);
    }

    public static String generateBillNumber(int sequence) {
        return formatNumber("BIL", sequence);
    }

    public static String generatePaymentNumber(int sequence) {
        return formatNumber("PAY", sequence);
    }

    public static String formatNumber(String prefix, int sequence) {
        int year = Year.now().getValue();
        return String.format("%s-%d-%04d", prefix, year, sequence);
    }

    public static int extractSequence(String latestNumber, String prefix) {
        if (latestNumber == null || latestNumber.isBlank()) {
            return 1;
        }
        String expectedPrefix = prefix + "-" + Year.now().getValue() + "-";
        if (!latestNumber.startsWith(expectedPrefix)) {
            return 1;
        }
        try {
            return Integer.parseInt(latestNumber.substring(expectedPrefix.length())) + 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
