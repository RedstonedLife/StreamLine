package net.plasmere.streamline.objects.exceptions;

public class OfflinePlayerException extends Exception {
    public OfflinePlayerException() {
        super("SavablePlayer is offline!");
    }
}
