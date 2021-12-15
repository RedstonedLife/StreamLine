package net.plasmere.streamline.objects.timers;

import net.plasmere.streamline.StreamLine;
import net.plasmere.streamline.objects.savable.users.SavablePlayer;
import net.plasmere.streamline.utils.sql.DataSource;

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



            return true;
        }

        return false;
    }
}
