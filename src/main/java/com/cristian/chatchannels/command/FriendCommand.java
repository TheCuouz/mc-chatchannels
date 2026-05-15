package com.cristian.chatchannels.command;

import com.cristian.chatchannels.ChatChannelsPlugin;
import com.cristian.chatchannels.friends.FriendManager;
import com.cristian.chatchannels.friends.FriendRequest;
import com.ttsstudio.sdk.PluginIdentity;
import com.ttsstudio.sdk.chat.ChatPrefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FriendCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final int PAGE_SIZE = 10;

    private final ChatChannelsPlugin plugin;
    private final PluginIdentity identity;

    public FriendCommand(ChatChannelsPlugin plugin) {
        this.plugin = plugin;
        this.identity = PluginIdentity.of(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ChatPrefix.error(sender, identity, "Only players can use this command.");
            return true;
        }
        if (!player.hasPermission("chatchannels.friends")) {
            ChatPrefix.error(player, identity, "No tienes permiso.");
            return true;
        }
        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        FriendManager fm = plugin.getFriendManager();
        switch (args[0].toLowerCase()) {
            case "add"      -> handleAdd(player, fm, args);
            case "accept"   -> handleAccept(player, fm, args);
            case "deny"     -> handleDeny(player, fm, args);
            case "remove"   -> handleRemove(player, fm, args);
            case "list"     -> handleList(player, fm, args);
            case "requests" -> handleRequests(player, fm);
            case "notify"   -> handleNotify(player, fm);
            default         -> sendUsage(player);
        }
        return true;
    }

    private void handleAdd(Player player, FriendManager fm, String[] args) {
        if (args.length < 2) {
            ChatPrefix.send(player, identity, "<red>Uso: /friend add <jugador>"); return;
        }
        String targetName = args[1];
        if (targetName.equalsIgnoreCase(player.getName())) {
            ChatPrefix.send(player, identity, plugin.getMessages().get("friend-self")); return;
        }
        if (!fm.canAddFriend(player.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-max", "max",
                    String.valueOf(plugin.getConfigManager().friendsMaxFriends())));
            return;
        }
        var offline = Bukkit.getOfflinePlayerIfCached(targetName);
        if (offline == null) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("pm-offline", "player", targetName)); return;
        }
        if (!fm.canAddFriend(offline.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-target-max", "player", targetName)); return;
        }
        if (fm.areFriends(player.getUniqueId(), offline.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-already", "player", targetName)); return;
        }
        if (!fm.addRequest(player.getUniqueId(), player.getName(), offline.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-request-exists", "player", targetName)); return;
        }
        ChatPrefix.send(player, identity,
            plugin.getMessages().get("friend-request-sent", "player", targetName));
        Player target = Bukkit.getPlayer(offline.getUniqueId());
        if (target != null) deliverRequest(target, player.getName());
    }

    private void handleAccept(Player player, FriendManager fm, String[] args) {
        if (args.length < 2) {
            ChatPrefix.send(player, identity, "<red>Uso: /friend accept <jugador>"); return;
        }
        String senderName = args[1];
        var offline = Bukkit.getOfflinePlayerIfCached(senderName);
        if (offline == null || !fm.hasPendingRequest(offline.getUniqueId(), player.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-no-request", "player", senderName)); return;
        }
        if (!fm.acceptRequest(offline.getUniqueId(), player.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-no-request", "player", senderName)); return;
        }
        ChatPrefix.send(player, identity,
            plugin.getMessages().get("friend-accepted", "player", senderName));
        Player requester = Bukkit.getPlayer(offline.getUniqueId());
        if (requester != null)
            ChatPrefix.send(requester, identity,
                plugin.getMessages().get("friend-accepted-other", "player", player.getName()));
    }

    private void handleDeny(Player player, FriendManager fm, String[] args) {
        if (args.length < 2) {
            ChatPrefix.send(player, identity, "<red>Uso: /friend deny <jugador>"); return;
        }
        String senderName = args[1];
        var offline = Bukkit.getOfflinePlayerIfCached(senderName);
        if (offline == null || !fm.hasPendingRequest(offline.getUniqueId(), player.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-no-request", "player", senderName)); return;
        }
        if (!fm.denyRequest(offline.getUniqueId(), player.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-no-request", "player", senderName)); return;
        }
        ChatPrefix.send(player, identity,
            plugin.getMessages().get("friend-denied", "player", senderName));
    }

    private void handleRemove(Player player, FriendManager fm, String[] args) {
        if (args.length < 2) {
            ChatPrefix.send(player, identity, "<red>Uso: /friend remove <jugador>"); return;
        }
        String targetName = args[1];
        var offline = Bukkit.getOfflinePlayerIfCached(targetName);
        if (offline == null || !fm.areFriends(player.getUniqueId(), offline.getUniqueId())) {
            ChatPrefix.send(player, identity,
                plugin.getMessages().get("friend-not-friends", "player", targetName)); return;
        }
        fm.removeFriendship(player.getUniqueId(), offline.getUniqueId());
        ChatPrefix.send(player, identity,
            plugin.getMessages().get("friend-removed", "player", targetName));
    }

    private void handleList(Player player, FriendManager fm, String[] args) {
        int page = 1;
        if (args.length >= 2) {
            try { page = Math.max(1, Integer.parseInt(args[1])); } catch (NumberFormatException ignored) {}
        }
        List<UUID> friendList = new ArrayList<>(fm.getFriends(player.getUniqueId()));
        int total = friendList.size();
        int maxFriends = plugin.getConfigManager().friendsMaxFriends();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
        page = Math.min(page, totalPages);
        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, total);

        player.sendMessage(MM.deserialize(
            "<gray>── Tus amigos (" + total + "/" + maxFriends + ") ───────────────────"));
        if (total == 0) {
            player.sendMessage(MM.deserialize(plugin.getMessages().get("friend-list-empty")));
        } else {
            for (int i = start; i < end; i++) {
                UUID fUuid = friendList.get(i);
                Player online = Bukkit.getPlayer(fUuid);
                String offlineName = online == null ? Bukkit.getOfflinePlayer(fUuid).getName() : null;
                String fName = online != null ? online.getName()
                    : (offlineName != null ? offlineName : fUuid.toString());
                if (online != null) {
                    Component nameComp = Component.text("  ● " + fName, NamedTextColor.GREEN)
                        .hoverEvent(HoverEvent.showText(Component.text("Click para enviar PM")));
                    Component pmBtn = Component.text(" [→ PM]", NamedTextColor.GOLD)
                        .clickEvent(ClickEvent.suggestCommand("/msg " + fName + " "))
                        .hoverEvent(HoverEvent.showText(Component.text("Enviar mensaje privado")));
                    player.sendMessage(nameComp.append(pmBtn));
                } else {
                    player.sendMessage(Component.text("  ◌ " + fName, NamedTextColor.GRAY)
                        .append(Component.text(" offline", NamedTextColor.DARK_GRAY)));
                }
            }
        }
        player.sendMessage(MM.deserialize(
            "<gray>  Página " + page + "/" + totalPages +
            (totalPages > page
                ? "  •  <click:run_command:'/friend list " + (page + 1) + "'><yellow>/friend list "
                  + (page + 1) + "</yellow></click>"
                : "")));
    }

    private void handleRequests(Player player, FriendManager fm) {
        List<FriendRequest> requests = fm.getPendingRequestsFor(player.getUniqueId());
        if (requests.isEmpty()) {
            ChatPrefix.send(player, identity, plugin.getMessages().get("friend-requests-empty"));
            return;
        }
        player.sendMessage(MM.deserialize("<gold>Solicitudes pendientes (" + requests.size() + "):"));
        for (FriendRequest req : requests) {
            deliverRequest(player, req.senderName());
        }
    }

    private void handleNotify(Player player, FriendManager fm) {
        boolean current = fm.getNotify(player.getUniqueId());
        fm.setNotify(player.getUniqueId(), !current);
        String key = current ? "friend-notify-off" : "friend-notify-on";
        ChatPrefix.send(player, identity, plugin.getMessages().get(key));
    }

    /** Sends the clickable friend request notification to the given online player. */
    public void deliverRequest(Player receiver, String senderName) {
        receiver.sendMessage(MM.deserialize(
            plugin.getMessages().get("friend-request-received", "player", senderName)));
        Component accept = Component.text("  [✔ Aceptar]", NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/friend accept " + senderName))
            .hoverEvent(HoverEvent.showText(Component.text("Aceptar solicitud de " + senderName)));
        Component deny = Component.text("   [✘ Rechazar]", NamedTextColor.RED)
            .clickEvent(ClickEvent.runCommand("/friend deny " + senderName))
            .hoverEvent(HoverEvent.showText(Component.text("Rechazar solicitud de " + senderName)));
        receiver.sendMessage(accept.append(deny));
    }

    private void sendUsage(Player player) {
        ChatPrefix.send(player, identity,
            "<gray>/friend <add|accept|deny|remove|list|requests|notify>");
    }
}
