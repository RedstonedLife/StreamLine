package net.plasmere.streamline.utils.objects;

import net.plasmere.streamline.StreamLine;

public class CustomSQLInfo {
    public String identifier;
    public String host;
    public String sql;

    public CustomSQLInfo(String identifier, String host, String sql) {
        this.identifier = identifier;
        this.host = host;
        this.sql = sql;
    }

    public Host getHostAsHost() {
        StreamLine.msbConfig.reloadHosts();

        for (Host host : StreamLine.msbConfig.loadedHosts) {
            if (host.identifier.equals(this.host)) return host;
        }

        return null;
    }
}
