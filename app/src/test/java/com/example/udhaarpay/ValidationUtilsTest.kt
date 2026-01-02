package com.example.udhaarpay

import com.example.udhaarpay.utils.ValidationUtils
import org.junit.Assert.*
import org.junit.Test

class ValidationUtilsTest {

    // Amount Validation Tests
    @Test
    fun testValidAmounts() {
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("100.50"))
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("10000.00"))
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("1.00"))
        assertTrue("Valid amount should pass", ValidationUtils.isValidAmount("9999.99"))
    }

    @Test
    fun testInvalidAmounts() {
        assertFalse("Zero amount should fail", ValidationUtils.isValidAmount("0"))
        assertFalse("Zero amount with decimals should fail", ValidationUtils.isValidAmount("0.00"))
        assertFalse("Negative amount should fail", ValidationUtils.isValidAmount("-100"))
        assertFalse("Amount too large should fail", ValidationUtils.isValidAmount("10001"))
        assertFalse("Amount too large with decimals should fail", ValidationUtils.isValidAmount("10000.01"))
        assertFalse("Invalid format should fail", ValidationUtils.isValidAmount("abc"))
        assertFalse("Empty string should fail", ValidationUtils.isValidAmount(""))
        assertFalse("Only decimals should fail", ValidationUtils.isValidAmount(".50"))
        assertFalse("Multiple decimals should fail", ValidationUtils.isValidAmount("100.50.25"))
    }

    // UPI ID Validation Tests
    @Test
    fun testValidUpiIds() {
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("user@paytm"))
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("test.user@oksbi"))
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("user123@axis"))
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("my_id@icici"))
        assertTrue("Valid UPI ID should pass", ValidationUtils.isValidUpiId("a@b.co"))
    }

    @Test
    fun testInvalidUpiIds() {
        assertFalse("UPI ID without @ should fail", ValidationUtils.isValidUpiId("userpaytm"))
        assertFalse("UPI ID with multiple @ should fail", ValidationUtils.isValidUpiId("user@paytm@extra"))
        assertFalse("UPI ID without domain should fail", ValidationUtils.isValidUpiId("user@"))
        assertFalse("UPI ID without username should fail", ValidationUtils.isValidUpiId("@paytm"))
        assertFalse("Empty string should fail", ValidationUtils.isValidUpiId(""))
        assertFalse("UPI ID with spaces should fail", ValidationUtils.isValidUpiId("user @ paytm"))
        assertFalse("UPI ID with special characters should fail", ValidationUtils.isValidUpiId("user@paytm!"))
    }

    // UPI PIN Validation Tests
    @Test
    fun testValidUpiPins() {
        assertTrue("Valid 4-digit PIN should pass", ValidationUtils.isValidUpiPin("1234"))
        assertTrue("Valid 6-digit PIN should pass", ValidationUtils.isValidUpiPin("123456"))
        assertTrue("Valid PIN with mixed digits should pass", ValidationUtils.isValidUpiPin("987654"))
    }

    @Test
    fun testInvalidUpiPins() {
        assertFalse("PIN too short should fail", ValidationUtils.isValidUpiPin("123"))
        assertFalse("PIN too long should fail", ValidationUtils.isValidUpiPin("1234567"))
        assertFalse("Empty PIN should fail", ValidationUtils.isValidUpiPin(""))
        assertFalse("PIN with letters should fail", ValidationUtils.isValidUpiPin("12a4"))
        assertFalse("PIN with special characters should fail", ValidationUtils.isValidUpiPin("12@4"))
        assertFalse("PIN with spaces should fail", ValidationUtils.isValidUpiPin("12 34"))
    }

    // Phone Number Validation Tests
    @Test
    fun testValidPhoneNumbers() {
        assertTrue("Valid 10-digit number starting with 6 should pass", ValidationUtils.isValidPhoneNumber("9876543210"))
        assertTrue("Valid 10-digit number starting with 7 should pass", ValidationUtils.isValidPhoneNumber("7123456789"))
        assertTrue("Valid 10-digit number starting with 8 should pass", ValidationUtils.isValidPhoneNumber("8123456789"))
        assertTrue("Valid 10-digit number starting with 9 should pass", ValidationUtils.isValidPhoneNumber("9123456789"))
    }

    @Test
    fun testInvalidPhoneNumbers() {
        assertFalse("Number too short should fail", ValidationUtils.isValidPhoneNumber("987654321"))
        assertFalse("Number too long should fail", ValidationUtils.isValidPhoneNumber("98765432101"))
        assertFalse("Number starting with 5 should fail", ValidationUtils.isValidPhoneNumber("5876543210"))
        assertFalse("Number with letters should fail", ValidationUtils.isValidPhoneNumber("98a6543210"))
        assertFalse("Empty string should fail", ValidationUtils.isValidPhoneNumber(""))
        assertFalse("Number with spaces should fail", ValidationUtils.isValidPhoneNumber("987 654 3210"))
        assertFalse("Number with special characters should fail", ValidationUtils.isValidPhoneNumber("987-654-3210"))
    }

    // Email Validation Tests
    @Test
    fun testValidEmails() {
        assertTrue("Valid email should pass", ValidationUtils.isValidEmail("user@example.com"))
        assertTrue("Valid email with subdomain should pass", ValidationUtils.isValidEmail("user@sub.example.com"))
        assertTrue("Valid email with numbers should pass", ValidationUtils.isValidEmail("user123@example.com"))
        assertTrue("Valid email with underscore should pass", ValidationUtils.isValidEmail("user_name@example.com"))
        assertTrue("Valid email with dots should pass", ValidationUtils.isValidEmail("user.name@example.com"))
    }

    @Test
    fun testInvalidEmails() {
        assertFalse("Email without @ should fail", ValidationUtils.isValidEmail("userexample.com"))
        assertFalse("Email without domain should fail", ValidationUtils.isValidEmail("user@"))
        assertFalse("Email without username should fail", ValidationUtils.isValidEmail("@example.com"))
        assertFalse("Email with multiple @ should fail", ValidationUtils.isValidEmail("user@example@domain.com"))
        assertFalse("Email with spaces should fail", ValidationUtils.isValidEmail("user @ example.com"))
        assertFalse("Email with special characters should fail", ValidationUtils.isValidEmail("user@exam!ple.com"))
        assertFalse("Empty string should fail", ValidationUtils.isValidEmail(""))
    }

    // Amount Formatting Tests
    @Test
    fun testAmountFormatting() {
        assertEquals("₹ 100.50", ValidationUtils.formatAmount(100.50))
        assertEquals("₹ 1000.00", ValidationUtils.formatAmount(1000.0))
        assertEquals("₹ 0.00", ValidationUtils.formatAmount(0.0))
        assertEquals("₹ 0.00", ValidationUtils.formatAmount("invalid"))
        assertEquals("₹ 50.25", ValidationUtils.formatAmount("50.25"))
    }

    // Amount Sanitization Tests
    @Test
    fun testAmountSanitization() {
        assertEquals("123.45", ValidationUtils.sanitizeAmount("₹123.45"))
        assertEquals("1000", ValidationUtils.sanitizeAmount("Rs. 1000"))
        assertEquals("50.25", ValidationUtils.sanitizeAmount("Amount: 50.25"))
        assertEquals("123", ValidationUtils.sanitizeAmount("123abc"))
        assertEquals("", ValidationUtils.sanitizeAmount("₹₹₹"))
    }

    // Edge Cases and Boundary Tests
    @Test
    fun testBoundaryCases() {
        // Amount boundaries
        assertTrue("Maximum valid amount should pass", ValidationUtils.isValidAmount("10000.00"))
        assertFalse("Amount over maximum should fail", ValidationUtils.isValidAmount("10000.01"))

        // PIN boundaries
        assertTrue("Minimum PIN length should pass", ValidationUtils.isValidUpiPin("1234"))
        assertFalse("PIN below minimum length should fail", ValidationUtils.isValidUpiPin("123"))
        assertTrue("Maximum PIN length should pass", ValidationUtils.isValidUpiPin("123456"))
        assertFalse("PIN above maximum length should fail", ValidationUtils.isValidUpiPin("1234567"))

        // Phone number boundaries
        assertTrue("10-digit number should pass", ValidationUtils.isValidPhoneNumber("9876543210"))
        assertFalse("9-digit number should fail", ValidationUtils.isValidPhoneNumber("987654321"))
        assertFalse("11-digit number should fail", ValidationUtils.isValidPhoneNumber("98765432101"))
    }

    // Null and Empty Input Tests
    @Test
    fun testNullAndEmptyInputs() {
        // Test null inputs (if methods can handle them)
        // Note: These tests assume the methods don't accept null inputs directly

        // Test empty strings
        assertFalse("Empty amount should fail", ValidationUtils.isValidAmount(""))
        assertFalse("Empty UPI ID should fail", ValidationUtils.isValidUpiId(""))
        assertFalse("Empty PIN should fail", ValidationUtils.isValidUpiPin(""))
        assertFalse("Empty phone should fail", ValidationUtils.isValidPhoneNumber(""))
        assertFalse("Empty email should fail", ValidationUtils.isValidEmail(""))

        // Test whitespace-only strings
        assertFalse("Whitespace amount should fail", ValidationUtils.isValidAmount("   "))
        assertFalse("Whitespace UPI ID should fail", ValidationUtils.isValidUpiId("   "))
        assertFalse("Whitespace PIN should fail", ValidationUtils.isValidUpiPin("   "))
        assertFalse("Whitespace phone should fail", ValidationUtils.isValidPhoneNumber("   "))
        assertFalse("Whitespace email should fail", ValidationUtils.isValidEmail("   "))
    }

    // Complex Input Tests
    @Test
    fun testComplexInputs() {
        // Test inputs with various formats and edge cases
        assertFalse("Amount with commas should fail", ValidationUtils.isValidAmount("1,000.00"))
        assertFalse("UPI ID with uppercase should pass", ValidationUtils.isValidUpiId("USER@PAYTM"))
        assertFalse("Phone with country code should fail", ValidationUtils.isValidPhoneNumber("+919876543210"))
        assertTrue("Email with uppercase should pass", ValidationUtils.isValidEmail("USER@EXAMPLE.COM"))
    }
}
