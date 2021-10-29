package net.plasmere.streamline.events.enums;

import java.util.Locale;

public enum Action {
    DEFAULT,
    SEND_MESSAGE_TO,
    SEND_MESSAGE_AS,
    SEND_SERVER,
    KICK,
    RUN_COMMAND_AS_OP,
    RUN_COMMAND_AS_SELF,
    GIVE_POINTS,
    TAKE_POINTS,
    SET_POINTS,
    ADD_TAG,
    REM_TAG,
    SEND_MESSAGE_TO_FRIENDS,
    SEND_MESSAGE_TO_PARTY_MEMBERS,
    SEND_MESSAGE_TO_GUILD_MEMBERS,
    SEND_MESSAGE_TO_STAFF,
    SEND_MESSAGE_TO_PERMISSION,
    RUN_SCRIPT
    ;

    public static String toString(Action action){
        try {
            return action.name();
        } catch (Exception e) {
            return DEFAULT.name();
        }
    }

    public static Action fromString(String action){
        action = action.toUpperCase(Locale.ROOT);

        try {
            return Action.valueOf(action);
        } catch (Exception e) {
            return DEFAULT;
        }
    }
}
