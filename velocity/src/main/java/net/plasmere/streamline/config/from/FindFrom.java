package net.plasmere.streamline.config.from;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.config.ConfigUtils;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.PluginUtils;

import java.io.FileWriter;

public class FindFrom {
    public static void doUpdate(String previousVersion, String language){

        if (PluginUtils.isFreshInstall()) {
            MessagingUtils.logWarning("Smells new in here!");
            MessagingUtils.logWarning("Please, report bugs on our Discord! Invite: https://discord.gg/tny494zXfn");

            StreamLine.constantsConfig.setFresh(false);
            return;
        }

        if (StreamLine.constantsConfig.streamlineConstants.isBeta) {
            MessagingUtils.logWarning("You are running a Beta Version!");
            MessagingUtils.logWarning("Please, report bugs on our Discord! Invite: https://discord.gg/tny494zXfn");
        } else {
            StreamLine.constantsConfig.setVersion(StreamLine.getVersion());
        }

        // TODO: MAKE SURE TO APPLY ALL PATCHES TO THE FIRST AND UP! (13.3 SHOULD HAVE ALL PATCHES APPLIED!)

        switch (previousVersion) {
            case "13.3":
                new From_1_0_13_3(language);
                new From_1_0_14_0(language);
                new From_1_0_14_2(language);
                new From_1_0_14_3(language);
                new From_1_0_14_5(language);
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.0":
                new From_1_0_14_0(language);
                new From_1_0_14_2(language);
                new From_1_0_14_3(language);
                new From_1_0_14_5(language);
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.1":
            case "1.0.14.2":
                new From_1_0_14_2(language);
                new From_1_0_14_3(language);
                new From_1_0_14_5(language);
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.3":
                new From_1_0_14_3(language);
                new From_1_0_14_5(language);
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.4":
            case "1.0.14.5":
                new From_1_0_14_5(language);
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.6":
            case "1.0.14.7":
            case "1.0.14.8":
                new From_1_0_14_8(language);
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.14.9":
                new From_1_0_14_9(language);
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.15.0":
                new From_1_0_15_0(language);
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.15.1":
            case "1.0.15.2":
                new From_1_0_15_2(language);
                new From_1_0_15_3(language);
                break;
            case "1.0.15.3":
                new From_1_0_15_3(language);
                break;
        }
    }
}
