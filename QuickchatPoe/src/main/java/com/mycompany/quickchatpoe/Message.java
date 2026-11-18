package com.mycompany.quickchatpoe;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class Message {
    private String messageID;
    private int messageNumber;
    private String sender;
    private String recipient;
    private String messageContent;
    private String messageHash;
    private String flag; // "Sent", "Stored", or "Disregard"

    public Message(int messageNumber, String sender, String recipient, String messageContent, String flag) {
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash();
        this.flag = flag;
    }

    // Constructor allowing explicit Message ID (useful for unit tests and JSON loading)
    public Message(String messageID, int messageNumber, String sender, String recipient, String messageContent, String flag) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.messageContent = messageContent;
        this.messageHash = createMessageHash();
        this.flag = flag;
    }

    // Randomly generated 10-digit Message ID
    private String generateMessageID() {
        Random rand = new Random();
        long id = 1000000000L + (long) (rand.nextDouble() * 9000000000L);
        return String.valueOf(id);
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    // Ensures recipient cell number starts with international code (+27) and is valid length
    public String checkRecipientCell(String cellNumber) {
        if (cellNumber != null && Pattern.matches("^\\+27[0-9]{9}$", cellNumber)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
    }

    // Validates message length (max 250 characters)
    public String checkMessageLength(String msg) {
        if (msg == null) return "Please enter a message of less than 250 characters.";
        if (msg.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = msg.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
    }

    // Autogenerates Message Hash: first 2 digits of ID : messageNumber : FIRSTLAST in caps
    public String createMessageHash() {
        if (messageID == null || messageID.length() < 2 || messageContent == null) return "";
        String prefix = messageID.substring(0, 2);

        String cleanMsg = messageContent.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        if (cleanMsg.isEmpty()) return prefix + ":" + messageNumber + ":";

        String[] words = cleanMsg.split("\\s+");
        String firstWord = words[0];
        String lastWord = words[words.length - 1];

        return prefix + ":" + messageNumber + ":" + (firstWord + lastWord).toUpperCase();
    }

    public String sentMessage(int option) {
        switch (option) {
            case 1:
                this.flag = "Sent";
                return "Message successfully sent.";
            case 2:
                this.flag = "Disregard";
                return "Press O to delete the message.";
            case 3:
                this.flag = "Stored";
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    public String printMessages() {
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + messageContent;
    }

    // Stores message into messages.json using org.json (Reference: org.json documentation)
    public void storeMessage(String filePath) {
        JSONArray jsonArray = new JSONArray();
        try {
            if (Files.exists(Paths.get(filePath))) {
                String existing = new String(Files.readAllBytes(Paths.get(filePath)));
                if (!existing.trim().isEmpty()) {
                    jsonArray = new JSONArray(existing);
                }
            }
            JSONObject obj = new JSONObject();
            obj.put("messageID", this.messageID);
            obj.put("messageNumber", this.messageNumber);
            obj.put("sender", this.sender);
            obj.put("recipient", this.recipient);
            obj.put("messageContent", this.messageContent);
            obj.put("messageHash", this.messageHash);
            obj.put("flag", this.flag);

            jsonArray.put(obj);

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(jsonArray.toString(4));
            }
        } catch (IOException e) {
            System.out.println("Error storing message to JSON: " + e.getMessage());
        }
    }

    // Reads messages back from JSON file into a list/array (Reference: org.json documentation)
    public static List<Message> readStoredMessagesFromJSON(String filePath) {
        List<Message> loadedMessages = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(filePath))) return loadedMessages;
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            if (content.trim().isEmpty()) return loadedMessages;

            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Message m = new Message(
                    obj.optString("messageID"),
                    obj.optInt("messageNumber"),
                    obj.optString("sender", "Developer"),
                    obj.optString("recipient"),
                    obj.optString("messageContent"),
                    obj.optString("flag")
                );
                loadedMessages.add(m);
            }
        } catch (Exception e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
        }
        return loadedMessages;
    }

    // Getters & Setters
    public String getMessageID() { return messageID; }
    public int getMessageNumber() { return messageNumber; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessageContent() { return messageContent; }
    public String getMessageHash() { return messageHash; }
    public String getFlag() { return flag; }
}