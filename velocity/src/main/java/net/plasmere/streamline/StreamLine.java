package net.plasmere.streamline;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
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
import net.plasmere.streamline.objects.configs.MSBConfig;
import net.plasmere.streamline.objects.configs.obj.AliasHandler;
import net.plasmere.streamline.objects.savable.groups.SavableGuild;
import net.plasmere.streamline.objects.configs.*;
import net.plasmere.streamline.objects.enums.NetworkState;
import net.plasmere.streamline.objects.messaging.DiscordMessage;
import net.plasmere.streamline.objects.savable.groups.SavableParty;
import net.plasmere.streamline.objects.savable.users.SavableConsole;
import net.plasmere.streamline.objects.timers.*;
import net.plasmere.streamline.placeholder.RATAPI;
import net.plasmere.streamline.placeholder.addons.StreamlineExpansion;
import net.plasmere.streamline.scripts.ScriptsHandler;
import net.plasmere.streamline.utils.*;
import net.plasmere.streamline.utils.holders.GeyserHolder;
import net.plasmere.streamline.utils.holders.LPHolder;
import net.plasmere.streamline.utils.holders.ViaHolder;
import net.plasmere.streamline.utils.holders.VoteHolder;
import net.plasmere.streamline.utils.sql.BridgerDataSource;
import net.plasmere.streamline.utils.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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

@Plugin(id = "streamline", name = "StreamLine", version = "${project.version}",
		url = "https://github.com/xnitrate/streamline/tree/velocity", description = "An Essentials plugin for Velocity!",
		authors = { "Nitrate" }, dependencies = {
		@Dependency(id = "luckperms", optional = true),
		@Dependency(id = "geyser", optional = true),
		@Dependency(id = "viaversion", optional = true),
		@Dependency(id = "nuvotifier", optional = true)
	}
)
public class StreamLine {
	private static ProxyServer server;
	private static Logger logger;
	private static Metrics.Factory metricsFactory;
	private static Path dataFolder;

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
	public static PlayTimeConf playTimeConf;
	public static RanksConfig ranksConfig;
	public static ChatFilters chatFilters;
	public static DatabaseInfo databaseInfo;
	public static TablistConfig tablistConfig;
	public static AliasConfig aliasConfig;
	public static TimedScriptsConfig timedScriptsConfig;

	public static MSBConfig msbConfig;
	public static RATAPI ratAPI;

	public static ConstantsConfig constantsConfig;

	public static StreamlineExpansion placeholderExpansion;

	public final static String customChannel = "streamline:channel";
	public final static String[] identifer = customChannel.split(":", 2);
	public final static ChannelIdentifier customIdentifier = MinecraftChannelIdentifier.create(identifer[0], identifer[1]);

	private static JDA jda = null;
	private static boolean isReady = false;

	private File plDir() { return new File(getDataFolder() + File.separator + "players" + File.separator); }
	private File gDir() { return new File(getDataFolder() + File.separator + "guilds" + File.separator); }
	private File pDir() { return new File(getDataFolder() + File.separator + "parties" + File.separator); }
	private File confDir() { return new File(getDataFolder() + File.separator + "configs" + File.separator); }
	private File chatHistoryDir() { return new File(getDataFolder() + File.separator + "chat-history" + File.separator); }
	private File scriptsDir() { return new File(getDataFolder() + File.separator + "scripts" + File.separator); }
	private File sqlDir() { return new File(getDataFolder() + File.separator + "sql" + File.separator); }
	private File eventsDir;

	public ScheduledTask guilds;
	public ScheduledTask players;
	public ScheduledTask clearCachedPlayers;
	public ScheduledTask saveCachedPlayers;
	public ScheduledTask playtime;
	public ScheduledTask oneSecTimer;
	public ScheduledTask motdUpdater;
	public ScheduledTask groupsDatabaseSyncer;

	private String currentMOTD;
	private int motdPage;

	@Inject
	public StreamLine(ProxyServer serverThing, Logger loggerThing, @DataDirectory Path dataFolderThing, Metrics.Factory metricsFactoryThing){
		server = serverThing;
		logger = loggerThing;
		dataFolder = dataFolderThing;
		instance = this;
		metricsFactory = metricsFactoryThing;
	}

	public File getPlDir() {
		return plDir();
	}
	public File getGDir() {
		return gDir();
	}
	public File getPDir() {
		return pDir();
	}
	public File getEDir() { return eventsDir; }
	public File getConfDir() { return confDir(); }
	public File getChatHistoryDir() { return chatHistoryDir(); }
	public File getScriptsDir() { return scriptsDir(); }
	public File getSQLDir() { return sqlDir(); }

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
			MessagingUtils.logWarning("An unknown error occurred building JDA...");
			return;
		}

		if (jda.getStatus() == JDA.Status.CONNECTED) {
			isReady = true;

			MessagingUtils.logInfo("JDA status is connected...");
		}
	}

	public void loadGuilds(){
		if (! gDir().exists()) {
			try {
				gDir().mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void loadParties(){
		if (! pDir().exists()) {
			try {
				pDir().mkdirs();
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public void loadPlayers(){
		if (! plDir().exists()) {
			try {
				plDir().mkdirs();
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
			guilds = server.getScheduler().buildTask(this, new GuildXPTimer(ConfigUtils.timePerGiveG())).repeat(1, TimeUnit.SECONDS).schedule();
			players = server.getScheduler().buildTask(this, new PlayerXPTimer(ConfigUtils.timePerGiveP())).repeat(1, TimeUnit.SECONDS).schedule();
			clearCachedPlayers = server.getScheduler().buildTask(this, new PlayerClearTimer(ConfigUtils.cachedPClear())).repeat(1, TimeUnit.SECONDS).schedule();
			saveCachedPlayers = server.getScheduler().buildTask(this, new PlayerSaveTimer(ConfigUtils.cachedPSave())).repeat(1, TimeUnit.SECONDS).schedule();
			playtime = server.getScheduler().buildTask(this, new PlaytimeTimer(1)).repeat(1, TimeUnit.SECONDS).schedule();
			oneSecTimer = server.getScheduler().buildTask(this, new OneSecondTimer()).repeat(1, TimeUnit.SECONDS).schedule();
			motdUpdater = server.getScheduler().buildTask(this, new MOTDUpdaterTimer(serverConfig.getMOTDTime())).repeat(1, TimeUnit.SECONDS).schedule();
			groupsDatabaseSyncer = server.getScheduler().buildTask(this, new GroupDatabaseSyncTimer(60)).repeat(1, TimeUnit.SECONDS).schedule();

			// DO NOT FORGET TO UPDATE AMOUNT BELOW! :/
			getLogger().info("Loaded 7 runnable(s) into memory...!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadServers(){
		if (! confDir().exists()) {
			try {
				confDir().mkdirs();
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

		constantsConfig = new ConstantsConfig();

		FindFrom.doUpdate(constantsConfig.streamlineConstants.version, constantsConfig.streamlineConstants.language);

		// Main config.
		config = new ConfigHandler(constantsConfig.streamlineConstants.language);

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

		playTimeConf = new PlayTimeConf();

		if (ConfigUtils.moduleBChatFiltersEnabled()) {
			if (ConfigUtils.scriptsEnabled()) {
				chatFilters = new ChatFilters();
			} else {
				MessagingUtils.logSevere("Could not enable chat filters because scripts are not enabled!");
			}
		}

		if (ConfigUtils.moduleDBUse()) {
			databaseInfo = new DatabaseInfo();
			DataSource.verifyTables();
		}

		if (ConfigUtils.mysqlbridgerEnabled()) {
			if (! sqlDir().exists()) if (! sqlDir().mkdirs()) MessagingUtils.logInfo("Could not properly make the SQL folder!");
			msbConfig = new MSBConfig();

			try {
				BridgerDataSource.reloadHikariHosts(); // Populate the Hikari Hosts.
			} catch (Exception e) {
				MessagingUtils.logSevere("Something is mis-configured! Caught error: " + e.getMessage());
			}
		}

		if (ConfigUtils.customTablistEnabled()) {
			tablistConfig = new TablistConfig();
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
		if (! chatHistoryDir().exists()) if (! chatHistoryDir().mkdirs()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Chat history folder could not be made!");

		if (ConfigUtils.chatHistoryLoadHistoryStartup()) {
			PlayerUtils.loadAllChatHistories(false);
		}
	}

	public void loadScripts() {
		if (! ConfigUtils.scriptsEnabled()) return;

		if (! scriptsDir().exists()) if (! scriptsDir().mkdirs()) if (ConfigUtils.debug()) MessagingUtils.logWarning("Scripts folder could not be made!");

		File file = new File(scriptsDir(), "boost.sl");

		if (! file.exists()) {
			if (ConfigUtils.scriptsCreateDefault()) {
				try (InputStream in = getResourceAsStream("boost.sl")) {
					Files.copy(in, file.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		File[] files = scriptsDir().listFiles();
		if (files != null) {
			for (File f : files) {
				if (! f.getName().endsWith(".sl")) continue;

				ScriptsHandler.addScript(f);
			}
		}

		timedScriptsConfig = new TimedScriptsConfig();

		if (ConfigUtils.customAliasesEnabled()) {
			aliasConfig = new AliasConfig();
			AliasHandler.loadAllAliasCommands();
		}
	}

	@Subscribe(order = PostOrder.LAST)
	public void onEnable(ProxyInitializeEvent event){
		PluginUtils.state = NetworkState.STARTING;

		instance = this;
		// Teller.
		getLogger().info("Loading version [v" + getVersion() + "]...");

		// LP Support.
		lpHolder = new LPHolder();

		// Via Support.
		viaHolder = new ViaHolder();

		// Geyser Support.
		geyserHolder = new GeyserHolder();

		getProxy().getChannelRegistrar().register(customIdentifier);

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
				getLogger().error("Streamline failed to load properly: " + e.getMessage() + ".");
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
			getLogger().error("Streamline server custom configs have been disabled due to no ViaVersion being detected.");
		}

		// Timers.
		loadTimers();

		// Set up SavableConsole.
		SavableConsole console = PlayerUtils.applyConsole();
		SavableGuild guild = GuildUtils.addGuildIfNotAlreadyLoaded(console);
		SavableParty party = PartyUtils.addPartyIfNotAlreadyLoaded(console);

		// Setting up SavablePlayer's HistorySave files.
		if (ConfigUtils.chatHistoryEnabled()) {
			loadChatHistory();
		}

		// Streamline PlaceholderAPI. (Replace a Thing API.)
		ratAPI = new RATAPI();
		placeholderExpansion = new StreamlineExpansion();

		PluginUtils.state = NetworkState.RUNNING;
		// Setup MOTD.
		StreamLine.getInstance().setCurrentMOTD(StreamLine.serverConfig.getComparedMOTD().firstEntry().getValue());

		Metrics metrics = metricsFactory.make(this, 13213);

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
	}

	@Subscribe
	public void onDisable(ProxyShutdownEvent event) {
		PluginUtils.state = NetworkState.STOPPING;

		String[] identifer = customChannel.split(":", 2);

		getProxy().getChannelRegistrar().unregister(MinecraftChannelIdentifier.create(identifer[0], identifer[1]));

		if (ConfigUtils.onCloseSafeKick() && ConfigUtils.onCloseKickMessage()) {
			PlayerUtils.kickAll(MessageConfUtils.kicksStopping());
		}

//		if (ConfigUtils.onCloseMain()) {
//			config.saveConf();
//			config.saveLocales();
//			config.saveDiscordBot();
//			config.saveCommands();
//		}
//
//		if (ConfigUtils.onCloseSettings()) {
//			serverConfig.saveConfig();
//		}

		guilds.cancel();
		players.cancel();
		playtime.cancel();
		clearCachedPlayers.cancel();
		saveCachedPlayers.cancel();
		oneSecTimer.cancel();
		motdUpdater.cancel();
		groupsDatabaseSyncer.cancel();

		if (ConfigUtils.customAliasesEnabled()) {
			AliasHandler.unloadAllAliasCommands();
		}

		try {
			if (ConfigUtils.moduleDEnabled()) {
				if (jda != null) {
					if (ConfigUtils.moduleShutdowns()) {
						try {
//						Objects.requireNonNull(jda.getTextChannelById(ConfigUtils.textChannelOfflineOnline)).sendMessageEmbeds(eb.setDescription("Bot shutting down...!").build()).queue();
							MessagingUtils.sendDiscordEBMessage(new DiscordMessage(getProxy().getConsoleCommandSource(), MessageConfUtils.shutdownTitle(), MessageConfUtils.shutdownMessage(), DiscordBotConfUtils.textChannelOfflineOnline()));
						} catch (Exception e) {
							getLogger().warn("An unknown error occurred with sending online message: " + e.getMessage());
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
						getLogger().warn("JDA took too long to shutdown, skipping!");
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		saveGuilds();
		saveParties();

		PluginUtils.state = NetworkState.STOPPED;
	}

	public void saveGuilds(){
		for (SavableGuild guild : GuildUtils.getGuilds()){
			guild.saveAll();
		}

		getLogger().info("Saved " + GuildUtils.getGuilds().size() + " Guilds!");
	}

	public void saveParties(){
		for (SavableParty party : PartyUtils.getParties()){
			party.saveAll();
		}

		getLogger().info("Saved " + PartyUtils.getParties().size() + " Parties!");
	}

	public static StreamLine getInstance() { return instance; }
	public static JDA getJda() { return jda; }
	public static boolean getIsReady() { return isReady; }

	public static void setReady(boolean ready) { isReady = ready; }

	public static ProxyServer getProxy() {
		return server;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static File getDataFolder() {
//		return FileSystems.getDefault().getPath("streamline").toFile();
		return dataFolder.toFile();
	}

	public InputStream getResourceAsStream(String filename) {
		return getClass().getClassLoader().getResourceAsStream(filename);
	}

	public static PluginDescription getDescription() {
		return getProxy().getPluginManager().getPlugin("streamline").get().getDescription();
	}

	public static String getVersion() {
		return "${project.version}";
	}

	// Utils Getters.
//	public static DiscordUtils getDiscordUtils() {
//		return ;
//	}
}
