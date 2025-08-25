package me.petterim1.discordchat;

public interface MessageHandler {

    /**
     * For IGNChat
     */
    void handle(String role, @Deprecated String timestamp, String discordname, String message);
}
