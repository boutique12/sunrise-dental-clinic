package com.sunrise.dental.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashPasswordProducesVerifiableFormat() {
        String hash = PasswordUtil.hashPassword("Sunrise@123");
        assertTrue(hash.matches("\\d+:.+:.+"));
        assertTrue(PasswordUtil.verifyPassword("Sunrise@123", hash));
    }

    @Test
    void verifyPasswordRejectsWrongPassword() {
        String hash = PasswordUtil.hashPassword("correct-password");
        assertFalse(PasswordUtil.verifyPassword("wrong-password", hash));
    }

    @Test
    void verifyPasswordAcceptsStoredHashFormat() {
        String plain = "demo-password-2026";
        String storedHash = PasswordUtil.hashPassword(plain);
        assertTrue(PasswordUtil.verifyPassword(plain, storedHash));
    }

    @Test
    void hashPasswordGeneratesUniqueSalts() {
        String hashOne = PasswordUtil.hashPassword("same-password");
        String hashTwo = PasswordUtil.hashPassword("same-password");
        assertNotEquals(hashOne, hashTwo);
    }
}
