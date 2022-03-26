package net.plasmere.streamline.utils.objects;

import net.plasmere.streamline.StreamLine;

public class PullAndPushInfo {
    public String column;
    public String host;
    public String table;
    public String where;

    public PullAndPushInfo(String column, String host, String table, String where) {
        this.column = column;
        this.host = host;
        this.table = table;
        this.where = where;
    }

    public Host getHostAsHost() {
        StreamLine.msbConfig.reloadHosts();

        for (Host host : StreamLine.msbConfig.loadedHosts) {
            if (host.identifier.equals(this.host)) return host;
        }

        return null;
    }
}
