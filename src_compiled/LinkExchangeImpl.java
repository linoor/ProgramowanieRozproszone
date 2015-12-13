import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 12/12/15.
 */
public class LinkExchangeImpl extends LinkExchangeSystemPOA {

    private ORB orb;

    private static AtomicInteger userIdNum = new AtomicInteger(0);

    @Override
    public void register(String username, IntHolder userID) {
        userID.value = userIdNum.getAndIncrement();
    }

    @Override
    public void addLink(int userID, String link, IntHolder linkID) {

    }

    @Override
    public boolean linkRemove(int userID, int linkID) {
        return false;
    }

    @Override
    public boolean publishLink(int userID, int linkID) {
        return false;
    }

    @Override
    public String[] getLinks(int userID) {
        return new String[0];
    }

    public void setOrb(ORB orb) {
        this.orb = orb;
    }
}
