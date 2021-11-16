package net.plasmere.streamline.services;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
	
	/*================================= Permissions =================================*/	
	public static CompletableFuture<Boolean> checkPermission(UUID uuid, String permission) {
		return luckPerms.getUserManager().loadUser(uuid).thenApplyAsync(user -> { 
			Tristate result = user.getCachedData().getPermissionData().checkPermission(permission);
			
			if(result == Tristate.TRUE) {
				return true;
			} else if(result == Tristate.FALSE) {
				return false;
			}
			
			return null;
		});
	}	
	
	public static boolean hasPermission(UUID uuid, String permission) {
		Boolean hasPermission = checkPermission(uuid, permission).join();
		return hasPermission != null && hasPermission;
	}

	public static void setPermission(UUID uuid, String permission) {
		getUser(uuid).thenAcceptAsync(user -> {
			PermissionNode node = PermissionNode.builder(permission).build();
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}

	public static void unsetPermission(UUID uuid, String permission) {
		getUser(uuid).thenAcceptAsync(user -> {	
			PermissionNode node = PermissionNode.builder(permission).value(false).build();
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}

	public static void deletePermission(UUID uuid, String permission) {
		getUser(uuid).thenAcceptAsync(user -> {
			user.data().clear(NodeType.PERMISSION.predicate(node -> node.getPermission().equals(permission)));
			luckPerms.getUserManager().saveUser(user);
		});
	}
	
	/*=================================== Groups ===================================*/
	public static boolean hasGroup(UUID uuid, String groupName) {
		return hasPermission(uuid, "group." + groupName);
	}
	
	public static void setGroup(UUID uuid, String groupName) {
		getUser(uuid).thenAcceptAsync(user -> {
			InheritanceNode node = InheritanceNode.builder(groupName).build();
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}
	
	public static void unsetGroup(UUID uuid, String groupName) {
		getUser(uuid).thenAcceptAsync(user -> {
			InheritanceNode node = InheritanceNode.builder(groupName).value(false).build();
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}
	
	public static CompletableFuture<Collection<Group>> getPlayerGroups(UUID uuid) {
		return luckPerms.getUserManager().loadUser(uuid)
				.thenApplyAsync(user -> user.getInheritedGroups(user.getQueryOptions()));
	}

	public static CompletableFuture<Set<String>> getPlayerGroupNames(UUID uuid) {
		return luckPerms.getUserManager().loadUser(uuid).thenApplyAsync(user ->
				user.getNodes().stream()
				.filter(NodeType.INHERITANCE::matches)
				.map(NodeType.INHERITANCE::cast)
				.map(InheritanceNode::getGroupName)
				.collect(Collectors.toSet())
		);
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

	public static CompletableFuture<Map<String, String>> getPrefixes(UUID uuid) {
		return luckPerms.getUserManager().loadUser(uuid).thenApplyAsync(user -> {
			Collection<Group> groups = getPlayerGroups(uuid).join();
			Map<String, String> prefixes = new HashMap<>();

			for(Group group : groups) {
				String name = group.getName();
				String prefix = group.getCachedData().getMetaData().getPrefix();
				if(prefix != null && !prefix.isEmpty()) {
					prefixes.put(name, prefix);
				}
			}
			return prefixes;
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

	/*================================= ChatColor =================================*/
	public static CompletableFuture<String> getChatColor(UUID uuid) {
		return getUser(uuid).thenApplyAsync(user -> {
			String chatcolor = user.getCachedData().getMetaData().getMetaValue("chat-color");
			if(chatcolor == null) {
				return "";
			}
			return chatcolor;
		});
	}

	public static void updateChatColor(UUID uuid, String color) {
		getUser(uuid).thenAcceptAsync(user -> {
			MetaNode node = MetaNode.builder("chat-color", color).build();
			user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("chat-color")));
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}

	/*================================= Nickname =================================*/
	public static CompletableFuture<String> getNickname(UUID uuid) {
		return getUser(uuid).thenApplyAsync(user -> {
			String nick = user.getCachedData().getMetaData().getMetaValue("nick");
			if(nick == null) {
				return "";
			}

			return nick;
		});
	}

	public static void updateNickname(UUID uuid, String nick) {
		getUser(uuid).thenAcceptAsync(user -> {
			MetaNode node = MetaNode.builder("nick", nick).build();
			user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("nick")));
			user.data().add(node);
			luckPerms.getUserManager().saveUser(user);
		});
	}

	public static void resetNickname(UUID uuid) {
		getUser(uuid).thenAcceptAsync(user -> {
			user.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals("nick")));
			luckPerms.getUserManager().saveUser(user);
		});
	}
}
