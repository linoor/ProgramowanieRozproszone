import org.omg.CORBA.IntHolder;

/**
 * Created by linoor on 12/12/15.
 */
public class Start extends LinkExchangeSystemPOA {

    @Override
    public void register(String username, IntHolder userID) {

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
}
