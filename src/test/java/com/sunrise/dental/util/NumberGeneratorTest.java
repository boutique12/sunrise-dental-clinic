package com.sunrise.dental.util;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumberGeneratorTest {

    @Test
    void generatePatientNumberUsesCurrentYearAndSequence() {
        int year = Year.now().getValue();
        assertEquals("PAT-" + year + "-0001", NumberGenerator.generatePatientNumber(1));
        assertEquals("PAT-" + year + "-0042", NumberGenerator.generatePatientNumber(42));
    }

    @Test
    void extractSequenceIncrementsFromLatestNumber() {
        int year = Year.now().getValue();
        String latest = "APT-" + year + "-0010";
        assertEquals(11, NumberGenerator.extractSequence(latest, "APT"));
    }

    @Test
    void extractSequenceReturnsOneWhenNoPreviousNumber() {
        assertEquals(1, NumberGenerator.extractSequence(null, "BIL"));
        assertEquals(1, NumberGenerator.extractSequence("", "PAY"));
    }

    @Test
    void extractSequenceReturnsOneForDifferentYearPrefix() {
        assertEquals(1, NumberGenerator.extractSequence("PAT-2020-0099", "PAT"));
    }
}
