package net.plasmere.streamline.utils.objects;

import net.plasmere.streamline.objects.savable.users.SavableUser;
import net.plasmere.streamline.utils.sql.BridgerDataSource;

public class Syncable {
    public String identifier;
    public boolean isString;
    public PullAndPushInfo pullFrom;
    public PullAndPushInfo pushTo;

    public Syncable(String identifier, boolean isString, PullAndPushInfo pullFrom, PullAndPushInfo pushTo) {
        this.identifier = identifier;
        this.isString = isString;
        this.pullFrom = pullFrom;
        this.pushTo = pushTo;
    }

    public String execute(SavableUser on) {
        return BridgerDataSource.sync(this, on);
    }
}
