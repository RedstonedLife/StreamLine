package net.plasmere.streamline;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.plasmere.streamline.config.ConfigHandler;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.DiscordBotConfUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.config.from.FindFrom;
import net.plasmere.streamline.discordbot.BoostListener;
import net.plasmere.streamline.discordbot.MemberListener;
import net.plasmere.streamline.discordbot.MessageListener;
import net.plasmere.streamline.discordbot.ReadyListener;
import net.plasmere.streamline.events.Event;
import net.plasmere.streamline.events.EventsHandler;
import net.plasmere.streamline.events.EventsReader;
import net.plasmere.streamline.libs.Metrics;
import net.plasmere.streamline.listeners.LPListener;
import net.plasmere.streamline.objects.SavableGuild;
import net.plasmere.streamline.objects.configs.*;
import net.plasmere.streamline.objects.enums.NetworkState;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.timers.*;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.*;
import net.plasmere.streamline.utils.holders.GeyserHolder;
import net.plasmere.streamline.utils.holders.LPHolder;
import net.plasmere.streamline.utils.holders.ViaHolder;
import net.plasmere.streamline.utils.holders.VoteHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StreamLine extends Plugin {
	private static StreamLine instance = null;

	public static ConfigHandler config;
	public static Bans bans;
	public static ServerPermissions serverPermissions;
	public static Lobbies lobbies;
	public static ViaHolder viaHolder;
	public static GeyserHolder geyserHolder;
	public static VoteHolder voteHolder;
	public static LPHolder lpHolder;
	public static ServerConfig serverConfig;
	public static DiscordData discordData;
	public static OfflineStats offlineStats;
	public static ChatConfig chatConfig;
	public static Votes votes;
	public static RanksConfig ranksConfig;
	public static ChatFilters chatFilters;
	public static DatabaseInfo databaseInfo;

	public final static String customChannel = "streamline:channel";

	private static JDA jda = null;
	private static boolean isReady = false;

	private final File plDir = new File(getDataFolder() + File.separator + "players" + File.separator);
	private final File gDir = new File(getDataFolder() + File.separator + "guilds" + File.separator);
	private final File pDir = new File(getDataFolder() + File.separator + "parties" + File.separator);
	private final File confDir = new File(getDataFolder() + File.separator + "configs" + File.separator);
	private final File chatHistoryDir = new File(getDataFolder() + File.separator + "chat-history" + File.separator);
	private final File scriptsDir = new File(getDataFolder() + File.separator + "scripts" + File.separator);
	private File eventsDir;

	public final File versionFile = new File(getDataFolder(), "version.txt");
	public final File languageFile = new File(getDataFolder(), "language.txt");

	public ScheduledTask guilds;
	public ScheduledTask players;
	public ScheduledTask clearCachedPlayers;
	public ScheduledTask saveCachedPlayers;
	public ScheduledTask playtime;
	public ScheduledTask oneSecTimer;
	public ScheduledTask motdUpdater;

	private String currentMOTD;
	private int motdPage;

	public StreamLine(){
		instance = this;
	}

	public File getPlDir() {
		return plDir;
	}
	public File getGDir() {
		return gDir;
	}
	public File getPDir() {
		return pDir;
	}
	public File getEDir() { return eventsDir; }
	public File getConfDir() { return confDir; }
	public File getChatHistoryDir() { return chatHistoryDir; }
	public File getScriptsDir() { return scriptsDir; }

	public String getCurrentMOTD() { return currentMOTD; }
	public int getMotdPage() { return motdPage; }
	public void setCurrentMOTD(String motd) { this.currentMOTD = motd; }
	public void setMotdPage(int page) { this.motdPage = page; }

    private void initJDA(){
		if (jda != null) try { jda.shutdownNow(); jda = null; } catch (Exception e) { e.printStackTrace(); }

		try {
			JDABuilder jdaBuilder = JDABuilder.create(DiscordBotConfUtils.botToken(),
							GatewayIntent.GUILD_MESSAGES,
							GatewayIntent.GUILD_MEMBERS,
							GatewayIntent.GUILD_PRESENCES,
							GatewayIntent.GUILD_VOICE_STATES,
							GatewayIntent.GUILD_EMOJIS
					)
					.setActivity(Activity.playing(DiscordBotConfUtils.botStatusMessage()));
			jdaBuilder.addEventListeners(
					new MessageListener(),
					new ReadyListener(),
					new BoostListener(),
					new MemberListener()
			);
			jda = jdaBuilder.build().awaitReady();
		} catch (Exception e) {
			getLogger().warning("An unknown error occurred building JDA...");
			return;
		}

		if (jda.getStatus() == JDA.Status.CONNECTED) {
			isReady = true;

			getLogger().info("JDA status is connected...");
		}
	}

	public void loadGuilds(){
		if (! gDir.exists()) {
			try {
				gDir.mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void loadParties(){
		if (! pDir.exists()) {
			try {
				pDir.mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void loadPlayers(){
		if (! plDir.exists()) {
			try {
				plDir.mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void loadEvents(){
		if (! ConfigUtils.events()) return;

		eventsDir = new File(getDataFolder() + File.separator + ConfigUtils.eventsFolder() + File.separator);

		if (! eventsDir.exists()) {
			try {
				eventsDir.mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}

		if (ConfigUtils.eventsWhenEmpty()) {
			try	(InputStream in = getResourceAsStream("default.yml")) {
				Files.copy(in, Path.of(eventsDir.toPath() + File.separator + "default.yml"));
			} catch (FileAlreadyExistsException e){
				getLogger().info("Default event file already here, skipping...");
			} catch (IOException e){
				e.printStackTrace();
			}
		}

		try {
			List<Path> files = Files.walk(eventsDir.toPath()).filter(p -> p.toString().endsWith(".yml")).collect(Collectors.toList());

			for (Path file : files) {
				Event event = EventsReader.fromFile(file.toFile());

				if (event == null) continue;

				EventsHandler.addEvent(event);
			}

			getLogger().info("Loaded " + EventsHandler.getEvents().size() + " event(s) into memory...!");
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void loadTimers(){
		try {
			guilds = getProxy().getScheduler().schedule(this, new GuildXPTimer(ConfigUtils.timePerGiveG()), 0, 1, TimeUnit.SECONDS);
			players = getProxy().getScheduler().schedule(this, new PlayerXPTimer(ConfigUtils.timePerGiveP()), 0, 1, TimeUnit.SECONDS);
			clearCachedPlayers = getProxy().getScheduler().schedule(this, new PlayerClearTimer(ConfigUtils.cachedPClear()), 0, 1, TimeUnit.SECONDS);
			saveCachedPlayers = getProxy().getScheduler().schedule(this, new PlayerSaveTimer(ConfigUtils.cachedPSave()), 0, 1, TimeUnit.SECONDS);
			playtime = getProxy().getScheduler().schedule(this, new PlaytimeTimer(1), 0, 1, TimeUnit.SECONDS);
			oneSecTimer = getProxy().getScheduler().schedule(this, new OneSecondTimer(), 0, 1, TimeUnit.SECONDS);
			motdUpdater = getProxy().getScheduler().schedule(this, new MOTDUpdaterTimer(serverConfig.getMOTDTime()), 0, 1, TimeUnit.SECONDS);

			// DO NOT FORGET TO UPDATE AMOUNT BELOW! :/
			getLogger().info("Loaded 7 runnable(s) into memory...!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadServers(){
		if (! confDir.exists()) {
			try {
				confDir.mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		// Server Permissions.
		serverPermissions = new ServerPermissions(false);

		// Lobbies.
		if (ConfigUtils.lobbies()) {
			lobbies = new Lobbies(false);
		}
	}

	public void loadConfigs() {
		if (! getDataFolder().exists()) {
			if (getDataFolder().mkdirs()) {
				MessagingUtils.logInfo("Made folder: " + getDataFolder().getName());
			}
		}

		String version = "";
		String language = "";

		if (! PluginUtils.isFreshInstall()) {
			try {
				if (!versionFile.exists()) {
					if (!versionFile.createNewFile()) if (ConfigUtils.debug()) {
						MessagingUtils.logSevere("COULD NOT CREATE VERSION FILE!");
					}

					FileWriter writer = new FileWriter(versionFile);
					writer.write("13.3");
					writer.close();
				}

				if (versionFile.exists()) {
					Scanner reader = new Scanner(versionFile);

					while (reader.hasNextLine()) {
						String data = reader.nextLine();
						while (data.startsWith("#")) {
							data = reader.nextLine();
						}
						version = data;
					}

					reader.close();
				}

				if (version.equals("")) throw new Exception("Version file could not be read!");
			} catch (Exception e) {
				e.printStackTrace();
				version = "13.3";
			}

			try {
				if (!languageFile.exists()) {
					if (!languageFile.createNewFile()) if (ConfigUtils.debug()) {
						MessagingUtils.logSevere("COULD NOT CREATE LANGUAGE FILE!");
					}

					FileWriter writer = new FileWriter(languageFile);
					writer.write("# To define which language you want to use.\n");
					writer.write("# Current supported languages: en_US, fr_FR\n");
					writer.write("en_US");
					writer.close();
				}

				if (languageFile.exists()) {
					Scanner reader = new Scanner(languageFile);

					while (reader.hasNextLine()) {
						String data = reader.nextLine();
						while (data.startsWith("#")) {
							data = reader.nextLine();
						}
						language = data;
					}

					reader.close();
				}

				if (language.equals("")) throw new Exception("Language file could not be read!");
			} catch (Exception e) {
				e.printStackTrace();
				language = "en_US";
			}
		} else {
			try {
				if (!versionFile.createNewFile()) if (ConfigUtils.debug()) {
					MessagingUtils.logSevere("COULD NOT CREATE VERSION FILE!");
				}

				FileWriter writer = new FileWriter(versionFile);
				writer.write(getDescription().getVersion());
				writer.close();

				if (versionFile.exists()) {
					Scanner reader = new Scanner(versionFile);

					while (reader.hasNextLine()) {
						String data = reader.nextLine();
						while (data.startsWith("#")) {
							data = reader.nextLine();
						}
						version = data;
					}

					reader.close();
				}

				if (version.equals("")) throw new Exception("Version file could not be read!");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (!languageFile.createNewFile()) if (ConfigUtils.debug()) {
					MessagingUtils.logSevere("COULD NOT CREATE LANGUAGE FILE!");
				}

				FileWriter writer = new FileWriter(languageFile);
				writer.write("# To define which language you want to use.\n");
				writer.write("# Current supported languages: en_US, fr_FR\n");
				writer.write("en_US");
				writer.close();

				if (languageFile.exists()) {
					Scanner reader = new Scanner(languageFile);

					while (reader.hasNextLine()) {
						String data = reader.nextLine();
						while (data.startsWith("#")) {
							data = reader.nextLine();
						}
						language = data;
					}

					reader.close();
				}

				if (language.equals("")) throw new Exception("Language file could not be read!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		FindFrom.doUpdate(version, language);

		// Main config.
		config = new ConfigHandler(language);

		// Server ConfigHandler.
		if (ConfigUtils.sc()) {
			serverConfig = new ServerConfig();
		}

		if (ConfigUtils.customChats()) {
			chatConfig = new ChatConfig();
		}

		// Discord Data.
		if (ConfigUtils.moduleDPC() || ConfigUtils.boostsEnabled()) {
			discordData = new DiscordData();
		}

		if (ConfigUtils.moduleBRanksEnabled()) {
			ranksConfig = new RanksConfig();
			votes = new Votes();
		}

		if (ConfigUtils.moduleBChatFiltersEnabled()) {
			chatFilters = new ChatFilters();
		}

		if (ConfigUtils.moduleDBUse()) {
			databaseInfo = new DatabaseInfo();
		}

		if (ConfigUtils.moduleDEnabled()) {
			if (ConfigUtils.moduleDPC()) {
				if (lpHolder.enabled) {
					LPListener lpListener = new LPListener(lpHolder.api);
				}
			}
		}
	}

	public void loadChatHistory() {
		if (! chatHistoryDir.exists()) if (! chatHistoryDir.mkdirs()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Chat history folder could not be made!");

		if (ConfigUtils.chatHistoryLoadHistoryStartup()) {
			PlayerUtils.loadAllChatHistories(false);
		}
	}

	public void loadScripts() {
		if (! ConfigUtils.scriptsEnabled()) return;

		if (! scriptsDir.exists()) if (! scriptsDir.mkdirs()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Scripts folder could not be made!");

		File file = new File(scriptsDir, "boost.sl");

		if (! file.exists()) {
			if (ConfigUtils.scriptsCreateDefault()) {
				try (InputStream in = getResourceAsStream("boost.sl")) {
					Files.copy(in, file.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File[] files = scriptsDir.listFiles();
		if (files != null) {
			for (File f : files) {
				if (! f.getName().endsWith(".sl")) continue;

				ScriptsHandler.addScript(f);
			}
		}
	}

    public void onLoad(){
    	InstanceHolder.setInst(instance);
	}

	@Override
	public void onEnable(){
		PluginUtils.state = NetworkState.STARTING;

		instance = this;

		// LP Support.
		lpHolder = new LPHolder();

		// Via Support.
		viaHolder = new ViaHolder();

		// Geyser Support.
		geyserHolder = new GeyserHolder();

		getProxy().registerChannel(customChannel);

		MessagingUtils.logInfo("Package: " + getClass().getPackage().getName());

		// Teller.
		getLogger().info("Loading version [v" + getProxy().getPluginManager().getPlugin("StreamLine").getDescription().getVersion() + "]...");

		// Configs...
		loadConfigs();

		// NuVotifier Support.
		if (ConfigUtils.moduleBVotifierEnabled()) {
			voteHolder = new VoteHolder();
		}

		// Bans.
		if (ConfigUtils.punBans()) {
			bans = new Bans();
		}

		// Commands.
		PluginUtils.loadCommands(this);

		// Listeners.
		PluginUtils.loadListeners(this);

		// JDA init.
		if (ConfigUtils.moduleDEnabled()) {
			Thread initThread = new Thread(this::initJDA, "Streamline - Initialization");
			initThread.setUncaughtExceptionHandler((t, e) -> {
				e.printStackTrace();
				getLogger().severe("Streamline failed to load properly: " + e.getMessage() + ".");
			});
			initThread.start();
		}

		// Players.
		loadPlayers();

		// Guilds.
		loadGuilds();

		// Parties.
		loadParties();

		// Events.
		loadEvents();

		// Scripts.
		loadScripts();

		// Offline Stats.
		offlineStats = new OfflineStats();

		// Servers by Versions.
		if (viaHolder.enabled) {
			loadServers();
		} else {
			getLogger().severe("Streamline server custom configs have been disabled due to no ViaVersion being detected.");
		}

		// Timers.
		loadTimers();

		// Set up SavableConsole.
		SavableConsole console = PlayerUtils.applyConsole();
		if (GuildUtils.existsByUUID(console.guild)) {
			try {
				GuildUtils.addGuild(new SavableGuild(console.guild, false));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Setting up SavablePlayer's HistorySave files.
		if (ConfigUtils.chatHistoryEnabled()) {
			loadChatHistory();
		}

		PluginUtils.state = NetworkState.RUNNING;

		Metrics metrics = new Metrics(this, 13153);

		if (ConfigUtils.bstatsMakeDiscoverable()) {
			metrics.addCustomChart(new Metrics.SimplePie("server_discoverables", new Callable<String>() {
				@Override
				public String call() throws Exception {
					return ConfigUtils.bstatsDisvoverableAddress();
				}
			}));
		}

		metrics.addCustomChart(new Metrics.SimplePie("discord_enabled", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(ConfigUtils.moduleDEnabled());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("total_guilds", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(GuildUtils.allGuildsCount());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("loaded_guilds", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(GuildUtils.getGuilds().size());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("loaded_parties", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(PartyUtils.getParties().size());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("custom_chats_enabled", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(ConfigUtils.customChats());
			}
		}));

		metrics.addCustomChart(new Metrics.SimplePie("proxy_chat_enabled", new Callable<String>() {
			@Override
			public String call() throws Exception {
				return String.valueOf(serverConfig.getProxyChatEnabled());
			}
		}));
	}

	@Override
	public void onDisable() {
		PluginUtils.state = NetworkState.STOPPING;

		getProxy().unregisterChannel(customChannel);

		if (ConfigUtils.onCloseSafeKick() && ConfigUtils.onCloseKickMessage()) {
			PlayerUtils.kickAll(MessageConfUtils.kicksStopping());
		}

		if (ConfigUtils.onCloseMain()) {
			config.saveConf();
			config.saveLocales();
			config.saveDiscordBot();
			config.saveCommands();
		}

		if (ConfigUtils.onCloseSettings()) {
			serverConfig.saveConfig();
		}

		guilds.cancel();
		players.cancel();
		playtime.cancel();
		clearCachedPlayers.cancel();
		saveCachedPlayers.cancel();
		oneSecTimer.cancel();
		motdUpdater.cancel();

		try {
			if (ConfigUtils.moduleDEnabled()) {
				if (jda != null) {
					if (ConfigUtils.moduleShutdowns()) {
						try {
//						Objects.requireNonNull(jda.getTextChannelById(ConfigUtils.textChannelOfflineOnline())).sendMessageEmbeds(eb.setDescription("Bot shutting down...!").build()).queue();
							MessagingUtils.sendDiscordEBMessage(new DiscordMessage(getProxy().getConsole(), MessageConfUtils.shutdownTitle(), MessageConfUtils.shutdownMessage(), DiscordBotConfUtils.textChannelOfflineOnline()));
						} catch (Exception e) {
							getLogger().warning("An unknown error occurred with sending online message: " + e.getMessage());
						}
					}

					Thread.sleep(2000);

					jda.getEventManager().getRegisteredListeners().forEach(listener -> jda.getEventManager().unregister(listener));
					CompletableFuture<Void> shutdownTask = new CompletableFuture<>();
					jda.addEventListener(new ListenerAdapter() {
						@Override
						public void onShutdown(@NotNull ShutdownEvent event) {
							shutdownTask.complete(null);
						}
					});
					jda.shutdownNow();
					jda = null;

					try {
						shutdownTask.get(5, TimeUnit.SECONDS);
					} catch (Exception e) {
						getLogger().warning("JDA took too long to shutdown, skipping!");
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		saveGuilds();

		PluginUtils.state = NetworkState.STOPPED;
	}

	public void saveGuilds(){
		for (SavableGuild guild : GuildUtils.getGuilds()){
			try {
				guild.saveInfo();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		getLogger().info("Saved " + GuildUtils.getGuilds().size() + " Guilds!");
	}

	public static StreamLine getInstance() { return instance; }
	public static JDA getJda() { return jda; }
	public static boolean getIsReady() { return isReady; }

	public static void setReady(boolean ready) { isReady = ready; }

}
