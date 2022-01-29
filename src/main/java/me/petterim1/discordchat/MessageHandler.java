package me.petterim1.discordchat;

public interface MessageHandler {

    /**
     * For IGNChat
     */
    void handle(String role, String timestamp, String discordname, String message);
}
