package com.mycompany.quickchatpoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    private static final String JSON_FILE = "messages.json";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginSystem = new Login();

        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter First Name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = scanner.nextLine();

        System.out.print("Enter Username (must contain '_' and <= 5 chars): ");
        String username = scanner.nextLine();

        System.out.print("Enter Password (>= 8 chars, 1 capital, 1 number, 1 special): ");
        String password = scanner.nextLine();

        System.out.print("Enter Cell Phone Number (+27...): ");
        String cellNumber = scanner.nextLine();

        String regResult = loginSystem.registerUser(username, password, cellNumber, firstName, lastName);
        System.out.println(regResult);

        if (regResult.contains("successfully added.")) {
            System.out.println("\n=== LOGIN ===");
            System.out.print("Enter Username: ");
            String loginUser = scanner.nextLine();

            System.out.print("Enter Password: ");
            String loginPass = scanner.nextLine();

            boolean isLoggedIn = loginSystem.loginUser(loginUser, loginPass);
            System.out.println(loginSystem.returnLoginStatus(isLoggedIn, firstName, lastName));

            if (isLoggedIn) {
                runQuickChatMenu(scanner, firstName + " " + lastName);
            }
        }
        scanner.close();
    }

    private static void runQuickChatMenu(Scanner scanner, String currentUser) {
        System.out.println("\nWelcome to QuickChat.");

        List<Message> sessionMessages = new ArrayList<>();
        // Load previously stored JSON messages into memory
        sessionMessages.addAll(Message.readStoredMessagesFromJSON(JSON_FILE));

        MessageManager manager = new MessageManager();
        manager.populateArrays(sessionMessages);

        int totalSentCount = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages & Reports");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1-4).");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("How many messages do you wish to enter? ");
                    int count = Integer.parseInt(scanner.nextLine().trim());

                    for (int i = 0; i < count; i++) {
                        System.out.println("\n--- Entering Message " + (i + 1) + " ---");
                        Message validator = new Message(i, currentUser, "", "", "Sent");

                        String recipient;
                        while (true) {
                            System.out.print("Enter Recipient Cell Number (+27...): ");
                            recipient = scanner.nextLine().trim();
                            String cellCheck = validator.checkRecipientCell(recipient);
                            System.out.println(cellCheck);
                            if (cellCheck.equals("Cell phone number successfully captured.")) {
                                break;
                            }
                        }

                        String content;
                        while (true) {
                            System.out.print("Enter Message (max 250 characters): ");
                            content = scanner.nextLine();
                            String lenCheck = validator.checkMessageLength(content);
                            System.out.println(lenCheck);
                            if (lenCheck.equals("Message ready to send.")) {
                                break;
                            }
                        }

                        System.out.println("Choose action: 1) Send Message  2) Disregard Message  3) Store Message to send later");
                        int action = Integer.parseInt(scanner.nextLine().trim());

                        Message msg = new Message(i, currentUser, recipient, content, "Sent");
                        String actionResult = msg.sentMessage(action);
                        System.out.println(actionResult);

                        if (action == 1) {
                            totalSentCount++;
                        } else if (action == 3) {
                            msg.storeMessage(JSON_FILE);
                        }

                        sessionMessages.add(msg);
                        manager.populateArrays(sessionMessages);

                        System.out.println("\n--- Message Details ---");
                        System.out.println(msg.printMessages());
                    }
                    System.out.println("\nTotal messages sent: " + totalSentCount);
                    break;

                case 2:
                    System.out.println("Coming Soon.");
                    break;

                case 3:
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                case 4:
                    runStoredMessagesSubMenu(scanner, manager);
                    break;

                default:
                    System.out.println("Invalid choice. Please select 1-4.");
            }
        }
    }

    private static void runStoredMessagesSubMenu(Scanner scanner, MessageManager manager) {
        System.out.println("\n=== Stored Messages & Reports ===");
        System.out.println("a) Display sender and recipient of all stored messages");
        System.out.println("b) Display the longest message");
        System.out.println("c) Search for a message ID");
        System.out.println("d) Search all messages for a particular recipient");
        System.out.println("e) Delete a message using the message hash");
        System.out.println("f) Display report of full details");
        System.out.print("Select option (a-f): ");

        String subChoice = scanner.nextLine().trim().toLowerCase();

        switch (subChoice) {
            case "a":
                System.out.println(manager.displaySenderAndRecipientOfStored());
                break;
            case "b":
                System.out.println("Longest message: " + manager.getLongestMessage());
                break;
            case "c":
                System.out.print("Enter Message ID: ");
                String id = scanner.nextLine().trim();
                System.out.println(manager.searchByMessageID(id));
                break;
            case "d":
                System.out.print("Enter Recipient Cell Number: ");
                String rec = scanner.nextLine().trim();
                List<String> found = manager.searchByRecipient(rec);
                if (found.isEmpty()) {
                    System.out.println("No messages found for " + rec);
                } else {
                    for (String m : found) {
                        System.out.println("- " + m);
                    }
                }
                break;
            case "e":
                System.out.print("Enter Message Hash to delete: ");
                String hash = scanner.nextLine().trim();
                System.out.println(manager.deleteByMessageHash(hash));
                break;
            case "f":
                System.out.println("\n--- Sent Messages Report ---");
                System.out.println(manager.displaySentReport());
                System.out.println("\n--- All Messages Full Details ---");
                System.out.println(manager.displayAllStoredDetailsReport());
                break;
            default:
                System.out.println("Invalid sub-menu option.");
        }
    }
}

