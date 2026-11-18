package com.mycompany.quickchatpoe;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {
    // Part 3 Parallel & Category Arrays
    public String[] sentMessages = new String[0];
    public String[] disregardedMessages = new String[0];
    public String[] storedMessages = new String[0];
    public String[] messageHashes = new String[0];
    public String[] messageIDs = new String[0];

    // Keep track of full message objects for rich lookups
    private List<Message> messageRecords = new ArrayList<>();

    public void populateArrays(List<Message> messages) {
        this.messageRecords = new ArrayList<>(messages);

        List<String> sentList = new ArrayList<>();
        List<String> disregardList = new ArrayList<>();
        List<String> storedList = new ArrayList<>();
        List<String> hashList = new ArrayList<>();
        List<String> idList = new ArrayList<>();

        for (Message m : messages) {
            hashList.add(m.getMessageHash());
            idList.add(m.getMessageID());

            if ("Sent".equalsIgnoreCase(m.getFlag())) {
                sentList.add(m.getMessageContent());
            } else if ("Disregard".equalsIgnoreCase(m.getFlag())) {
                disregardList.add(m.getMessageContent());
            } else if ("Stored".equalsIgnoreCase(m.getFlag())) {
                storedList.add(m.getMessageContent());
            }
        }

        sentMessages = sentList.toArray(new String[0]);
        disregardedMessages = disregardList.toArray(new String[0]);
        storedMessages = storedList.toArray(new String[0]);
        messageHashes = hashList.toArray(new String[0]);
        messageIDs = idList.toArray(new String[0]);
    }

    // 2a. Display sender and recipient of all stored messages
    public String displaySenderAndRecipientOfStored() {
        StringBuilder sb = new StringBuilder();
        for (Message m : messageRecords) {
            if ("Stored".equalsIgnoreCase(m.getFlag())) {
                sb.append("Sender: ").append(m.getSender())
                  .append(" | Recipient: ").append(m.getRecipient()).append("\n");
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : "No stored messages found.";
    }

    // 2b. Display the longest message
    public String getLongestMessage() {
        String longest = "";
        for (Message m : messageRecords) {
            if (m.getMessageContent() != null && m.getMessageContent().length() > longest.length()) {
                longest = m.getMessageContent();
            }
        }
        return longest;
    }

    // 2c. Search for a message ID and return corresponding recipient and message
    public String searchByMessageID(String searchID) {
        for (Message m : messageRecords) {
            if (m.getMessageID().equals(searchID)) {
                return "Recipient: " + m.getRecipient() + " | Message: " + m.getMessageContent();
            }
        }
        return "Message ID not found.";
    }

    // 2d. Search for all messages sent or stored for a particular recipient
    public List<String> searchByRecipient(String recipientNumber) {
        List<String> results = new ArrayList<>();
        for (Message m : messageRecords) {
            if (m.getRecipient().equals(recipientNumber)) {
                results.add(m.getMessageContent());
            }
        }
        return results;
    }

    // 2e. Delete a message using the message hash
    public String deleteByMessageHash(String hash) {
        for (int i = 0; i < messageRecords.size(); i++) {
            if (messageRecords.get(i).getMessageHash().equalsIgnoreCase(hash)) {
                String deletedContent = messageRecords.get(i).getMessageContent();
                messageRecords.remove(i);
                populateArrays(messageRecords); // Refresh arrays after deletion
                return "Message \"" + deletedContent + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    // 2f. Display report listing full details of all sent messages
    public String displaySentReport() {
        StringBuilder report = new StringBuilder();
        for (Message m : messageRecords) {
            if ("Sent".equalsIgnoreCase(m.getFlag())) {
                report.append("Message Hash: ").append(m.getMessageHash())
                      .append(" | Recipient: ").append(m.getRecipient())
                      .append(" | Message: ").append(m.getMessageContent()).append("\n");
            }
        }
        return report.length() > 0 ? report.toString().trim() : "No sent messages to report.";
    }

    // Full report of all messages
    public String displayAllStoredDetailsReport() {
        StringBuilder report = new StringBuilder();
        for (Message m : messageRecords) {
            report.append("ID: ").append(m.getMessageID())
                  .append(" | Hash: ").append(m.getMessageHash())
                  .append(" | Sender: ").append(m.getSender())
                  .append(" | Recipient: ").append(m.getRecipient())
                  .append(" | Status: ").append(m.getFlag())
                  .append(" | Message: ").append(m.getMessageContent()).append("\n");
        }
        return report.length() > 0 ? report.toString().trim() : "No messages recorded.";
    }
}