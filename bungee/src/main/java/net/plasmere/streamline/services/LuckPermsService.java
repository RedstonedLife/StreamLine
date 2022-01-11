package net.plasmere.streamline.services;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LuckPermsService {
	private static LuckPerms luckPerms;
	
	public static void loadLuckPerms(LuckPerms luckPerms) {
		LuckPermsService.luckPerms = luckPerms;
	}
	
	/*================================= LuckPerms ==================================*/
	public static CompletableFuture<User> getUser(UUID uuid) {
		return luckPerms.getUserManager().loadUser(uuid)
				.thenApplyAsync(user -> user);
	}

	/*=================================== Groups ===================================*/
	public static CompletableFuture<Collection<Group>> getPlayerGroups(UUID uuid) {
		return luckPerms.getUserManager().loadUser(uuid)
				.thenApplyAsync(user -> user.getInheritedGroups(user.getQueryOptions()));
	}

	/*================================= Prefixes =================================*/
	public static CompletableFuture<String> getPrefix(UUID uuid) {
		return getUser(uuid).thenApplyAsync(user -> {
			String prefix = user.getCachedData().getMetaData().getPrefix();
			if(prefix == null) {
				prefix = "";
			}
			
			return prefix;
		});
	}

	/*================================= Suffix =================================*/
	public static CompletableFuture<String> getSuffix(UUID uuid) {
		return getUser(uuid).thenApplyAsync(user -> {
			String suffix = user.getCachedData().getMetaData().getSuffix();
			if(suffix == null) {
				suffix = "";
			}

			return suffix;
		});
	}

	public static CompletableFuture<String> getCombinedSuffix(UUID uuid) {
		return getPlayerGroups(uuid).thenApplyAsync(groups -> {
			String suffixes = "";

			for(Group group : groups) {
				String suffix = group.getCachedData().getMetaData().getSuffix();
				if(suffix != null && !suffix.isEmpty()) {
					suffixes += suffix;
				}
			}

			return suffixes;
		});
	}

	/*================================== Meta =================================*/
	public static CompletableFuture<String> getMeta(UUID uuid, String meta) {
		return getUser(uuid).thenApplyAsync(user ->
				user.getCachedData().getMetaData().getMetaValue(meta)
		);
	}

	public static CompletableFuture<String> getMetaContext(UUID uuid, String metaName) {
		return getUser(uuid).thenApplyAsync(user -> {
			Optional<QueryOptions> queryOptions = luckPerms.getContextManager().getQueryOptions(user);
			String meta = "";

			if(queryOptions.isPresent()) {
				meta = user.getCachedData().getMetaData(queryOptions.get()).getMetaValue(metaName);
				if (meta == null) {
					meta = "";
				}
			}

			return meta;
		});
	}
}
