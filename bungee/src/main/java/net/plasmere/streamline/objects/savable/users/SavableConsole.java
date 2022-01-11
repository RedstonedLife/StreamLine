package net.plasmere.streamline.objects.savable.users;

import net.md_5.bungee.api.ProxyServer;
import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class SavableConsole extends SavableUser {
    public ProxyServer server;

    public List<String> savedKeys = new ArrayList<>();

    public SavableConsole() {
        super("%", SavableAdapter.Type.CONSOLE);

        this.server = StreamLine.getInstance().getProxy();
    }

    @Override
    public List<String> getTagsFromConfig(){
        return ConfigUtils.consoleDefaultTags();
    }

    @Override
    public void populateMoreDefaults() {
        latestName = getOrSetDefault("profile.latest.name", ConfigUtils.consoleName());
        displayName = getOrSetDefault("profile.display-name", ConfigUtils.consoleDisplayName());
        tagList = getOrSetDefault("profile.tags", ConfigUtils.consoleDefaultTags());
    }

    @Override
    public void loadMoreValues() {

    }

    @Override
    public void saveMore() {

    }
}
