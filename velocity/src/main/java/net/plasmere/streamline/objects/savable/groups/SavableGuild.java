package net.plasmere.streamline.objects.savable.groups;

import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.utils.MathUtils;
import net.plasmere.streamline.utils.sql.DataSource;

public class SavableGuild extends SavableGroup {
    public String name;
    public int totalXP;
    public int currentXP;
    public int level;

    public int defaultLevel = ConfigUtils.guildExperienceStartingLevel();

    public SavableGuild(String creatorUUID, String name) {
        this(creatorUUID);

        this.name = name;
        saveAll();
    }

    public SavableGuild(String uuid) {
        super(uuid, SavableAdapter.Type.GUILD);
    }

    @Override
    public void populateMoreDefaults() {
        name = getOrSetDefault("guild.name", "");
        totalXP = getOrSetDefault("guild.stats.experience.total", ConfigUtils.guildExperienceStartingXP());
        currentXP = getOrSetDefault("guild.stats.experience.current", ConfigUtils.guildExperienceStartingXP());
        level = getOrSetDefault("guild.stats.level", defaultLevel);
    }

    @Override
    public void loadMoreValues(){
        name = getOrSetDefault("guild.name", name);
        totalXP = getOrSetDefault("guild.stats.experience.total", totalXP);
        currentXP = getOrSetDefault("guild.stats.experience.current", currentXP);
        level = getOrSetDefault("guild.stats.level", level);
    }

    @Override
    public void saveMore() {
        set("guild.name", name);
        set("guild.stats.experience.total", totalXP);
        set("guild.stats.experience.current", currentXP);
        set("guild.stats.level", level);
    }

    /*
   Experience required =
   2 × current_level + 7 (for levels 0–15)
   5 × current_level – 38 (for levels 16–30)
   9 × current_level – 158 (for levels 31+)
    */

    public int getNeededXp() {
//        loadValues();
        int needed = 0;

        String function =
                ConfigUtils.guildExperienceEquation()
                        .replace("%default_level%", String.valueOf(defaultLevel))
                        .replace("%guild_level%", String.valueOf(level))
                        .replace("%guild_current_xp%", String.valueOf(currentXP))
                        .replace("%guild_total_xp%", String.valueOf(totalXP));

        needed = (int) Math.round(MathUtils.eval(function));

        return needed;
    }

    public int xpUntilNextLevel(){
//        loadValues();
        return getNeededXp() - this.totalXP;
    }

    public void addTotalXP(int amount){
//        loadValues();
        setTotalXP(amount + this.totalXP);
    }

    public void setTotalXP(int amount){
//        loadValues();
        this.totalXP = amount;

        while (xpUntilNextLevel() <= 0) {
            int setLevel = this.level + 1;
            this.level = setLevel;
        }

        currentXP = getCurrentXP();

//        saveAll();
    }

    public int getCurrentLevelXP(){
//        loadValues();
        int xpTill = 0;
        for (int i = 0; i <= this.level; i++) {
            xpTill += getNeededXp();
        }

        return xpTill;
    }

    public int getCurrentXP(){
//        loadValues();
        return this.totalXP - getCurrentLevelXP();
    }


    public String setNameReturnOld(String newName) {
//        loadValues();
        String toReturn = this.name;
        this.name = newName;
        return toReturn;
    }
}
