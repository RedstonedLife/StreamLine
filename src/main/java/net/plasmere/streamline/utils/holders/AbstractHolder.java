package net.plasmere.streamline.utils.holders;

import net.md_5.bungee.api.ProxyServer;

public abstract class AbstractHolder<T> {
    String plugin;
    Class<?> api;

    AbstractHolder(String plugin) {
        this.plugin = plugin;
    }

    public boolean isPresent() {return (ProxyServer.getInstance().getPluginManager().getPlugin(this.plugin) != null);}
    public void setAPI(Class<?> api) {this.api = api;}
    public Class<?> getAPI() {return this.api;}
}
