/**
 * Created by linoor on 1/10/16.
 */
public class Ship {

    protected GameInterface gi;
    protected long playerId;
    protected int warshipId;

    public Ship(long playerId, GameInterface gi, int warshipId) {
        this.playerId = playerId;
        this.gi = gi;
        this.warshipId = warshipId;
    }
}
