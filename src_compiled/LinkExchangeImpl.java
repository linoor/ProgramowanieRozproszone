import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by linoor on 12/12/15.
 */
public class LinkExchangeImpl extends LinkExchangeSystemPOA {

    private ORB orb;

    private final static AtomicInteger userIdNum = new AtomicInteger(0);
    private final static AtomicInteger linkIdNum = new AtomicInteger(0);

    private final Map<String, Integer> users = new HashMap<>();

    @Override
    public void register(String username, IntHolder userID) {
        boolean userExists = false;
        synchronized (users) {
            if (users.containsKey(username)) {
                userExists = true;
            }
        }
        if (!userExists) {
            synchronized (users) {
                userID.value = userIdNum.getAndIncrement();
                users.put(username, userID.value);
            }
            System.out.println(String.format("Registered user %s with num %d", username, userID.value));
        } else {
            userID.value = -1;
        }
    }

    @Override
    public void addLink(int userID, String link, IntHolder linkID) {
        linkID.value = linkIdNum.getAndIncrement();
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
    private class Link {
        public boolean isPrivate = false;
        public int id = -1;

        public Link(int id) {
            this.id = id;
        }
    }
}
