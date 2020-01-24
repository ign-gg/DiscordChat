package discordchat;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

public class Main extends PluginBase {

    static Main instance;
    static Config config;
    static JDA jda;
    static String channelId;
    static String consoleChannelId;
    static boolean debug;
    static DiscordCommandSender discordCommandSender;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        config = getConfig();
        checkAndUpdateConfig();
        debug = config.getBoolean("debug");
        if (debug) getLogger().info("Registering events for PlayerListener");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        try {
            if (debug) getLogger().info("Logging in with bot token " + config.getString("botToken", "null"));
            jda = new JDABuilder(config.getString("botToken")).build();
            if (debug) getLogger().info("Waiting JDA...");
            jda.awaitReady();
            if (debug) getLogger().info("Setting server channel id to " + config.getString("channelId", "null"));
            channelId = config.getString("channelId");
            if (debug) getLogger().info("Registering events for DiscordListener");
            jda.addEventListener(new DiscordChatListener());
            if (config.getBoolean("discordConsole")) {
                if (debug) getLogger().info("Creating new DiscordCommandSender");
                discordCommandSender = new DiscordCommandSender();
                if (debug) getLogger().info("Setting console channel id to " + config.getString("consoleChannelId", "null"));
                consoleChannelId = config.getString("consoleChannelId");
                if (debug) getLogger().info("Registering events for DiscordConsole");
                jda.addEventListener(new DiscordConsoleListener());
            }
            if (!config.getString("botStatus").isEmpty()) {
                if (debug) getLogger().info("Setting bot status to " + config.getString("botStatus"));
                jda.getPresence().setActivity(Activity.of(Activity.ActivityType.DEFAULT, config.getString("botStatus")));
            }
            if (!config.getString("channelTopic").isEmpty()) {
                if (debug) getLogger().info("Setting channel topic to " + config.getString("channelTopic"));
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) {
                    ch.getManager().setTopic(config.getString("channelTopic")).queue();
                } else if (debug) {
                    getLogger().error("TextChannel is null");
                }
            }
            if (debug && jda.getGuilds().isEmpty()) getLogger().warn("Your Discord bot is not on any server");
            if (debug) getLogger().info("Startup done successfully");
        } catch (Exception e) {
            getLogger().error("Couldn't enable Discord chat sync");
            if (debug) e.printStackTrace();
        }
        if (config.getBoolean("startMessages")) API.sendMessage(config.getString("status_server_started"));
    }

    @Override
    public void onDisable() {
        if (config.getBoolean("stopMessages")) API.sendMessage(config.getString("status_server_stopped"));
        if (debug) getLogger().info("Disabling the plugin");
    }

    private void checkAndUpdateConfig() {
        if (config.getInt("configVersion") != 3) {
            if (config.getInt("configVersion") == 2) {
                config.set("commandPrefix", "!");
                config.set("configVersion", 3);
                config.save();
                config = getConfig();
                getLogger().warn("Config file updated.");
                return;
            }
            saveResource("config.yml", true);
            config = getConfig();
            getLogger().warn("Outdated config file replaced. You will need to set your settings again.");
        }
    }
}
