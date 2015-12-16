import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by linoor on 12/12/15.
 */
public class LinkExchangeImpl extends LinkExchangeSystemPOA {
    private ORB orb;

    private AtomicInteger idCounter = new AtomicInteger(0);
    private final Map<String, Integer> usernames = new HashMap<>();

    private AtomicInteger linkCounter = new AtomicInteger(0);
    private final Map<Integer, Link> links = new HashMap<>();

    public void setOrb(ORB orb) {
        this.orb = orb;
    }

    @Override
    public void register(String username, IntHolder userID) {
        synchronized (usernames) {
            if (usernames.containsKey(username)) {
                userID.value = -1;
            } else {
                userID.value = idCounter.getAndIncrement();
                usernames.put(username, userID.value);
            }
        }
    }

    @Override
    public void addLink(int userID, String link, IntHolder linkID) {
        synchronized (usernames) {
            if (!usernames.containsValue(userID)) {
                linkID.value = -1;
                return;
            }
        }

        synchronized (links) {
            linkID.value = linkCounter.getAndIncrement();
            links.put(linkID.value, new Link(link, linkID.value, userID));
        }
    }

    @Override
    public boolean linkRemove(int userID, int linkID) {
        synchronized (usernames) {
            if (!usernames.containsValue(userID)) {
                return false;
            }
        }

        synchronized (links) {
            if (!links.containsKey(linkID)) {
                return false;
            } else {
                links.remove(linkID);
                return true;
            }
        }
    }

    @Override
    public boolean publishLink(int userID, int linkID) {
        synchronized (usernames) {
            if (!usernames.containsValue(userID)) {
                return false;
            }
        }

        synchronized (links) {
            if (!links.containsKey(linkID)) {
                return false;
            } else {
                links.get(linkID).isPublished = true;
                return true;
            }
        }
    }

    @Override
    public String[] getLinks(int userID) {
        List<String> results = new ArrayList<>();
        synchronized (links) {
            results.addAll(links.values().stream()
                    .filter(link -> link.isPublished || link.userid == userID)
                    .map(link -> link.link).collect(Collectors.toList()));
        }

        if (results.size() == 0) {
            return new String[0];
        } else {
            String[] tmp = new String[results.size()];
            return results.toArray(tmp);
        }
    }

    private class Link {
        public String link;
        public int id;
        public int userid;
        public boolean isPublished = false;

        public Link(String link, int id, int userid) {
            this.link = link;
            this.id = id;
            this.userid = userid;
        }
    }
}
