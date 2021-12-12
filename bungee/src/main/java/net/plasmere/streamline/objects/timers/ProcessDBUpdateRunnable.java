package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.enums.SavableType;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.MessagingUtils;
import net.plasmere.streamline.utils.UUIDUtils;
import net.plasmere.streamline.utils.sql.DataSource;
import net.plasmere.streamline.utils.sql.Driver;

import java.util.concurrent.CompletableFuture;

public class ProcessDBUpdateRunnable implements Runnable {
    public SavablePlayer player;
    public boolean done;

    public ProcessDBUpdateRunnable(SavablePlayer player) {
        this.done = false;
        this.player = player;
    }

    @Override
    public void run() {
        if (! done) {
            done();
        }
    }

    public void done() {
        completeUpdatePlayerOnDB(player);
        this.done = true;
    }

    public void completeUpdatePlayerOnDB(SavablePlayer player) {
        CompletableFuture.supplyAsync(() -> updatePlayerOnDB(player));
    }

    public boolean updatePlayerOnDB(SavablePlayer player) {
        if (! StreamLine.databaseInfo.getHost().equals("")) {
//            Driver.update(SavableType.PLAYER, UUIDUtils.stripUUID(player.uuid));

            DataSource.updatePlayerData(player);

            return true;
        }

        return false;
    }
}
