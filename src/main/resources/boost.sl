# StreamLine scripts (*.sl) can have several variables:
# %player_absolute% = The player you are executing on's absolute name.
# %player_formatted% = The player you are executing on's just display name.
# %player_display% = The player you are executing on's off-on display name.
# %player_normal% = The player you are executing on's off-on absolute name.
# %player_guild_name% = The player you are executing on's guild's name.
# %player_guild_uuid% = The player you are executing on's guild leader's uuid.
# %sender_absolute% = The sender's absolute name.
# %sender_formatted% = The sender's just display name.
# %sender_display% = The sender's off-on display name.
# %sender_normal% = The sender's off-on absolute name.
# %sender_guild_name% = The sender's guild's name.
# %sender_guild_uuid% = The sender's guild leader's uuid.
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