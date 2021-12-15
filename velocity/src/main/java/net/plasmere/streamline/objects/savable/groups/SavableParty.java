package net.plasmere.streamline.objects.savable.groups;

import net.plasmere.streamline.objects.savable.SavableAdapter;
import net.plasmere.streamline.objects.savable.groups.SavableGroup;

public class SavableParty extends SavableGroup {
    public SavableParty(String creatorUUID, int maxSize) {
        this(creatorUUID);

        setMaxSize(maxSize);
    }

    public SavableParty(String uuid) {
        super(uuid, SavableAdapter.Type.PARTY);
    }

    @Override
    public void populateMoreDefaults() {

    }

    @Override
    public void loadMoreValues() {

    }

    @Override
    public void saveMore() {

    }
}