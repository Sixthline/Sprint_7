package model;

public class CancelOrder {
    private int track;

    public CancelOrder(int track) {
        this.track = track;
    }

    public CancelOrder() {
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }
}
