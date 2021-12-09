package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.enums.SavableType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.UUIDUtils;
import net.plasmere.streamline.utils.sql.Driver;

import java.util.concurrent.CompletableFuture;

public class ProcessDBUpdateRunnable implements Runnable {
    public SavablePlayer player;
    public boolean done;

    public ProcessDBUpdateRunnable(SavablePlayer player) {
//        MessagingUtils.logInfo("Runner 1");
        this.done = false;
        this.player = player;
    }

    @Override
    public void run() {
//        MessagingUtils.logInfo("Runner 2");
        if (! done) {
//            MessagingUtils.logInfo("Runner 3");
            done();
        }
    }

    public void done() {
//        MessagingUtils.logInfo("Runner 4");
        completeUpdatePlayerOnDB(player);
        this.done = true;
    }

    public void completeUpdatePlayerOnDB(SavablePlayer player) {
//        MessagingUtils.logInfo("Runner 5");
        CompletableFuture.supplyAsync(() -> updatePlayerOnDB(player));
    }

    public boolean updatePlayerOnDB(SavablePlayer player) {
//        MessagingUtils.logInfo("Runner 6");
        if (! StreamLine.databaseInfo.getHost().equals("")) {
            Driver.update(SavableType.PLAYER, UUIDUtils.stripUUID(player.uuid));
//            MessagingUtils.logInfo("Runner 7");
            return true;
        }

        return false;
    }
}
