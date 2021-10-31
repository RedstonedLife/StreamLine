# StreamLine scripts (*.sl) can have several variables:
# %player_uuid% = The player you are executing on's uuid.
# %player_points% = The player you are executing on's points.
# %player_prefix% = The player you are executing on's luckperms prefix.
# %player_suffix% = The player you are executing on's luckperms suffix.
# %player_absolute% = The player you are executing on's absolute name.
# %player_formatted% = The player you are executing on's just display name.
# %player_display% = The player you are executing on's off-on display name.
# %player_normal% = The player you are executing on's off-on absolute name.
# %player_guild_name% = The player you are executing on's guild's name.
# %player_guild_members% = The player you are executing on's guild's member count.
# %player_guild_leader_uuid% = The player you are executing on's guild leader's uuid.
# %player_guild_leader_absolute% = The player you are executing on's guild leader's absolute name.
# %player_guild_leader_formatted% = The player you are executing on's guild leader's just display name.
# %player_guild_leader_normal% = The player you are executing on's guild leader's off-on absolute name.
# %player_guild_leader_display% = The player you are executing on's guild leader's off-on display name.
#
# %sender_uuid% = The sender's uuid.
# %sender_points% = The sender's points.
# %sender_prefix% = The sender's luckperms prefix.
# %sender_suffix% = The sender's luckperms suffix.
# %sender_absolute% = The sender's absolute name.
# %sender_formatted% = The sender's just display name.
# %sender_display% = The sender's off-on display name.
# %sender_normal% = The sender's off-on absolute name.
# %sender_guild_name% = The sender's guild's name.
# %sender_guild_leader_uuid% = The sender's guild leader's uuid.
# %sender_guild_leader_absolute% = The sender's guild leader's absolute name.
# %sender_guild_leader_formatted% = The sender's guild leader's just display name.
# %sender_guild_leader_normal% = The sender's guild leader's off-on absolute name.
# %sender_guild_leader_display% = The sender's guild leader's off-on display name.
# Before conditions:
# '!' = run as operator.
# '?' = run as console.
# '.' = run as sender.
# '' = run as the player.
# Example: ?alert tacos are cool
# --> This would send an alert as the console say "tacos are cool".
# Or: lobby survival
# --> This would send the player you are executing on to the survival server as if they ran the command.
?lpb user %player_absolute% parent set booster