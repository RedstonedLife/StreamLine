package net.plasmere.streamline.placeholder.addons;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.config.MessageConfUtils;
import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.placeholder.RATExpansion;
import net.plasmere.streamline.utils.PlayerUtils;

public class StreamlineExpansion extends RATExpansion {
    public StreamlineExpansion() {
        super("streamline", "Quaint", "0.0.0.1");
    }

    @Override
    public String onLogic(String params) {
        if (params.equals("prefix")) return MessageConfUtils.prefix();
        if (params.equals("version")) return StreamLine.getInstance().getDescription().getVersion();
        if (params.equals("players_online")) return String.valueOf(PlayerUtils.getOnlinePPlayers().size());
        if (params.equals("players_loaded")) return String.valueOf(PlayerUtils.getStats().size());
        if (params.equals("staff_online")) return String.valueOf(PlayerUtils.getJustStaffOnline().size());

        if (params.matches("([a][u][t][h][o][r][\\[]([0-2])[\\]])")) {
            Pattern pattern = Pattern.compile("([a][u][t][h][o][r][\\[]([0-9])[\\]])");
            Matcher matcher = pattern.matcher(params);
            while (matcher.find()) {
                return StreamLine.getInstance().getDescription().getAuthor();
            }
        }

        if (params.matches("([s][t][a][f][f][\\[]([0-" + (PlayerUtils.getNamesJustStaffOnline().size() - 1) + "])[\\]])")) {
            Pattern pattern = Pattern.compile("([s][t][a][f][f][\\[]([0-" + (PlayerUtils.getNamesJustStaffOnline().size() - 1) + "])[\\]])");
            Matcher matcher = pattern.matcher(params);
            while (matcher.find()) {
                return PlayerUtils.getNamesJustStaffOnline().get(Integer.parseInt(matcher.group(2)));
            }
        }

        return null;
    }

    @Override
    public String onRequest(SavableUser user, String params) {
        if (params.equals("ping")) {
            if (user.online) return String.valueOf(PlayerUtils.getPPlayer(user.uuid).getPing());
            else return "Offline";
        }

        if (params.equals("check_staff")) return user.hasPermission(ConfigUtils.staffPerm()) ? "True" : "False";
        if (params.equals("check_online")) return user.online ? "True" : "False";

        return null;
    }
}