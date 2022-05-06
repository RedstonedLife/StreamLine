package net.plasmere.streamline.utils.holders;

import net.luckperms.api.LuckPermsProvider;
import net.plasmere.streamline.utils.MessagingUtils;

public class LPHolder extends AbstractHolder<LuckPermsProvider> {

    public LPHolder() {
        super("LuckPerms");
        if(super.isPresent())
            try {super.setAPI(LuckPermsProvider.get().getClass());}
            catch(Exception e) {MessagingUtils.logSevere("LuckPerms not loaded... Disabling LuckPerms support...");} 
    }

}
