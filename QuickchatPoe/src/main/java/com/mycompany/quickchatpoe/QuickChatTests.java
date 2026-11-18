package com.mycompany.quickchatpoe;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuickChatTests {

    @Test
    public void testPart1LoginAndRegistration() {
        Login login = new Login();
        assertTrue(login.checkUserName("kyl_1"));
        assertFalse(login.checkUserName("kyle!!!!!!!"));

        assertTrue(login.checkPasswordComplexity("Ch&&sec@ke99!"));
        assertFalse(login.checkPasswordComplexity("password"));

        assertTrue(login.checkCellPhoneNumber("+27838968976"));
        assertFalse(login.checkCellPhoneNumber("08966553"));

        login.registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976", "Kyle", "Smith");
        assertTrue(login.loginUser("kyl_1", "Ch&&sec@ke99!"));
        assertFalse(login.loginUser("wrong", "pass"));
        System.out.println("✅ Part 1: Login & Registration Tests Passed!");
    }

    @Test
    public void testPart2MessageValidationAndHash() {
        Message msg1 = new Message("0012345678", 0, "Developer", "+27718693002",
                "Hi Mike, can you join us for dinner tonight?", "Sent");

        assertEquals("Message ready to send.", msg1.checkMessageLength(msg1.getMessageContent()));
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell(msg1.getRecipient()));
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());

        String overLimit = "A".repeat(255);
        assertEquals("Message exceeds 250 characters by 5; please reduce the size.", msg1.checkMessageLength(overLimit));

        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",
                msg1.checkRecipientCell("08575975889"));

        assertEquals("Message successfully sent.", msg1.sentMessage(1));
        assertEquals("Press O to delete the message.", msg1.sentMessage(2));
        assertEquals("Message successfully stored.", msg1.sentMessage(3));
        System.out.println("✅ Part 2: Message Validation & Hash Tests Passed!");
    }

    @Test
    public void testPart3ArraysSearchDeleteAndReport() {
        List<Message> testMessages = new ArrayList<>();
        testMessages.add(new Message("1000000001", 0, "Developer", "+27834557896", "Did you get the cake?", "Sent"));
        testMessages.add(new Message("1000000002", 1, "Developer", "+27838884567", "Where are you? You are late! I have asked you to be on time.", "Stored"));
        testMessages.add(new Message("1000000003", 2, "Developer", "+27834484567", "Yohoooo, I am at your gate.", "Disregard"));
        testMessages.add(new Message("0838884567", 3, "Developer", "0838884567", "It is dinner time !", "Sent"));
        testMessages.add(new Message("1000000005", 4, "Developer", "+27838884567", "Ok, I am leaving without you.", "Stored"));

        MessageManager manager = new MessageManager();
        manager.populateArrays(testMessages);

        assertArrayEquals(new String[]{"Did you get the cake?", "It is dinner time !"}, manager.sentMessages);
        assertEquals("Where are you? You are late! I have asked you to be on time.", manager.getLongestMessage());
        
        String idSearchResult = manager.searchByMessageID("0838884567");
        assertTrue(idSearchResult.contains("It is dinner time !"));

        List<String> recipientMsgs = manager.searchByRecipient("+27838884567");
        assertEquals(2, recipientMsgs.size());
        assertTrue(recipientMsgs.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(recipientMsgs.contains("Ok, I am leaving without you."));

        String msg2Hash = testMessages.get(1).getMessageHash();
        String deleteResponse = manager.deleteByMessageHash(msg2Hash);
        assertEquals("Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.", deleteResponse);

        String report = manager.displaySentReport();
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("+27834557896"));
        System.out.println("✅ Part 3: Arrays, Search, Delete & Report Tests Passed!");
    }

    // This main method allows you to run this file instantly using normal Run File (F6)
    public static void main(String[] args) {
        QuickChatTests runner = new QuickChatTests();
        System.out.println("🚀 Running QuickChat Tests manually...\n");
        try {
            runner.testPart1LoginAndRegistration();
            runner.testPart2MessageValidationAndHash();
            runner.testPart3ArraysSearchDeleteAndReport();
            System.out.println("\n🎉 SUCCESS: All test assertions passed successfully!");
        } catch (AssertionError e) {
            System.err.println("\n❌ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}