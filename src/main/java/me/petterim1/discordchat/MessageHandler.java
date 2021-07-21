package me.petterim1.discordchat;

public interface MessageHandler {

    void handle(String role, String timestamp, String discordname, String message);
}
