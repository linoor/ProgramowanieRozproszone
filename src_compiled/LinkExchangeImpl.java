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
    private final Map<Integer, Link> links = new HashMap<>();

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
        synchronized (users) {
            if (users.containsValue(userID)) {
                System.out.println(String.format("Added link %s to user %d", link, userID));
                linkID.value = linkIdNum.getAndIncrement();
                links.put(linkID.value, new Link(linkID.value, link));
            } else {
                System.out.println(String.format("User %d does not exist", userID));
                linkID.value = -1;
            }
        }
    }

    @Override
    public boolean linkRemove(int userID, int linkID) {
        synchronized (users) {
            if (!users.containsValue(userID)) {
                return false;
            }
        }
        synchronized (links) {
            if (!links.containsKey(linkID)) {
                return false;
            }
        }
        return true;
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
        private String link = "";
        public boolean isPrivate = false;
        public int id = -1;

        public Link(int id, String link) {
            this.id = id;
        }

        public String getLink() {
            return link;
        }
    }
}
