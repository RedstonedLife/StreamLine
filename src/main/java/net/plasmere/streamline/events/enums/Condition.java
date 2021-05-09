package net.plasmere.streamline.events.enums;

import java.util.Locale;

public enum Condition {
    JOIN,
    LEAVE,
    MESSAGE_EXACT,
    MESSAGE_CONTAINS,
    MESSAGE_STARTS_WITH,
    MESSAGE_ENDS_WITH,
    COMMAND,

    // Soft Conditions...

    SERVER,
    NAME_EXACT,
    NAME_CONTAINS,
    NAME_STARTS_WITH,
    NAME_ENDS_WITH
    ;

    public static String toString(Condition condition){
        switch (condition) {
            case JOIN:
                return "JOIN";
            case LEAVE:
                return "LEAVE";
            case MESSAGE_EXACT:
                return "MESSAGE_EXACT";
            case MESSAGE_CONTAINS:
                return "MESSAGE_CONTAINS";
            case MESSAGE_STARTS_WITH:
                return "MESSAGE_STARTS_WITH";
            case MESSAGE_ENDS_WITH:
                return "MESSAGE_ENDS_WITH";
            case COMMAND:
                return "COMMAND";

                // Soft conditions...

            case SERVER:
                return "SERVER";
            case NAME_EXACT:
                return "NAME_EXACT";
            case NAME_CONTAINS:
                return "NAME_CONTAINS";
            case NAME_STARTS_WITH:
                return "NAME_STARTS_WITH";
            case NAME_ENDS_WITH:
                return "NAME_ENDS_WITH";
            default:
                return "";
        }
    }

    public static Condition fromString(String condition){
        condition = condition.toUpperCase(Locale.ROOT);

        switch (condition) {
            case "JOIN":
                return JOIN;
            case "LEAVE":
                return LEAVE;
            case "MESSAGE_EXACT":
                return MESSAGE_EXACT;
            case "MESSAGE_CONTAINS":
                return MESSAGE_CONTAINS;
            case "MESSAGE_STARTS_WITH":
                return MESSAGE_STARTS_WITH;
            case "MESSAGE_ENDS_WITH":
                return MESSAGE_ENDS_WITH;
            case "COMMAND":
                return COMMAND;

                // Soft Conditions...

            case "SERVER":
                return SERVER;
            case "NAME_EXACT":
                return NAME_EXACT;
            case "NAME_CONTAINS":
                return NAME_CONTAINS;
            case "NAME_STARTS_WITH":
                return NAME_STARTS_WITH;
            case "NAME_ENDS_WITH":
                return NAME_ENDS_WITH;
            default:
                return null;
        }
    }
}
