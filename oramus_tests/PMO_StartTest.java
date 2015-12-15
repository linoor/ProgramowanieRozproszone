import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.CORBA.ORBPackage.InvalidName;

public class PMO_StartTest {

	private LinkExchangeSystem les;
	private CyclicBarrier barrier;
	private static final int THREADS = 20;
	private static final int LINKS_PER_USER = 20;

	private Set<Integer> ids;
	private Set<String> publicLinks;
	private AtomicBoolean okFlag;

	private void notifyMainThread() {
		synchronized (PMO_StartTest.class) {
			PMO_StartTest.class.notifyAll();
		}
	}

	private void connectToServer( String[] argv ) {
		PMO_SOUT.println( "Polaczenie do serwera - poczatek");
		ORB orb = ORB.init(argv, null);
		org.omg.CORBA.Object namingContextObj;
		try {
			namingContextObj = orb.resolve_initial_references("NameService");
			NamingContext namingContext = NamingContextHelper
					.narrow(namingContextObj);

			NameComponent[] path = { new NameComponent("LINKEXCHANGE", "Object") };

			org.omg.CORBA.Object envObj = namingContext.resolve(path);
			les = LinkExchangeSystemHelper.narrow(envObj);
			PMO_SOUT.println( "Polaczenie do serwera - OK");
			return;
		} catch (Exception e) {
			PMO_SOUT.println("Odebrano wyjatek juz w trakcie polaczenia do systemu");
			PMO_SOUT.println("Test polaczenia bezposrednio z LINKEXCHANGE");
			try {
				namingContextObj = orb.resolve_initial_references("NameService");
	            NamingContextExt ncRef = NamingContextExtHelper.narrow(namingContextObj);
	            String name = "LINKEXCHANGE";
	            NameComponent path[] = ncRef.to_name(name);
				org.omg.CORBA.Object envObj = ncRef.resolve(path);
				les = LinkExchangeSystemHelper.narrow(envObj);
				PMO_SOUT.println( "Polaczenie do serwera - OK");
				return;	            
			} catch (Exception e1) {
				PMO_SOUT.println("Podobnie bez skutku - nie mozna odszukac serwisu");
			}

			PMO_SOUT.println( "Brak polacznia z serwisem - nie mozna kontynuowac testu");
			System.exit(1);
		}		
	}
	
	private PMO_StartTest(String[] argv) {
		connectToServer( argv );
		okFlag = new AtomicBoolean(true);
		barrier = new CyclicBarrier(THREADS);
		ids = Collections.synchronizedSet(new HashSet<Integer>());
		publicLinks = Collections.synchronizedSet(new HashSet<String>());
	}

	class User implements Runnable {
		private int id;
		private String name;

		private List<String> links = new ArrayList<>(LINKS_PER_USER);
		private List<Integer> linkIDs = new ArrayList<>(LINKS_PER_USER);
		private List<Integer> linksToPublish;
		private Map<Integer, String> id2link = new HashMap<>();
		private Map<String, Integer> link2id = new HashMap<>();

		private void registration() throws InterruptedException,
				BrokenBarrierException {
			org.omg.CORBA.IntHolder userID = new IntHolder();

			// czekamy na pojawienie sie wszystkich klientow
			synchronizationByBarrier();

			try {
				// rejestracja
				les.register(name, userID);
			} catch (Exception e) {
				showErrorAndNotify("Doszlo do wyjatku w trakcie rejestracji uzytkownika "
						+ e);
			}

			id = userID.value;

			// klient dodaje wlasne ID do zbioru
			ids.add(id);

			// czekamy na wszystkich klientow
			synchronizationByBarrier();

			// sprawdzamy, czy klient uzyskuje unikalne id
			if (ids.size() != THREADS) {
				showErrorAndNotify("Uzytkownicy systemu nie otrzymali unikalnych ID, dalsza czesc testu nie ma juz sensu");
			} else {

			}
		}

		private void synchronizationByBarrier() {
			try {
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}

		private void showErrorAndNotify(String message) {
			synchronized (PMO_StartTest.class) {
				if (okFlag.get()) {
					okFlag.set(false);
					String e = "       _____                     _ \n"
							+ "      | ____|_ __ _ __ ___  _ __| |\n"
							+ "      |  _| | '__| '__/ _ \\| '__| |\n"
							+ "      | |___| |  | | | (_) | |  |_|\n"
							+ "      |_____|_|  |_|  \\___/|_|  (_)\n\n";

					System.out.println(e + message);
				}
				notifyMainThread();
			}
		}

		private void createLinks() {
			for (int i = 0; i < LINKS_PER_USER; i++) {
				links.add(name + "_" + (new Integer(i).toString()));
			}
		}

		private void addLinksToSystem() throws InterruptedException,
				BrokenBarrierException {
			synchronizationByBarrier();

			IntHolder ih = new IntHolder();
			for (int i = 0; i < links.size(); i++) {
				les.addLink(id, links.get(i), ih);
				linkIDs.add(new Integer(ih.value));
			}

			Set<Integer> testowyID = new HashSet<>(linkIDs);

			// czy liczba otrzymanych ID zgadza sie z liczba linkow?
			if (testowyID.size() != links.size()) {
				synchronized (PMO_SOUT.class) {
					for (int i = 0; i < links.size(); i++) {
						PMO_SOUT.println("Odebrane ID dla linku "
								+ links.get(i) + " -> " + linkIDs.get(i));
					}
				}
				showErrorAndNotify("Nie otrzymano unikalnych ID dla linkow usera "
						+ name);
			} else {
				PMO_SOUT.println("Odebrano poprawna liczbe IDs dla linkow usera "
						+ name);
			}

			// przygotowanie danych do kolejnych testow
			for (int i = 0; i < links.size(); i++) {
				id2link.put(linkIDs.get(i), links.get(i));
				link2id.put(links.get(i), linkIDs.get(i));
			}
		}

		private void testLinks(Collection<String> links) {
			testLinks(links, true);
		}

		private void testLinks(Collection<String> links, boolean testSizeFlag) {
			String[] linksReceived = les.getLinks(id);

			if (testSizeFlag)
				if (links.size() != linksReceived.length) {
					showErrorAndNotify("Liczba odebranych linkow nie jest zgodna z oczekiwana. Jest "
							+ linksReceived.length
							+ " a powinno "
							+ links.size());
				}

			List<String> receivedLinks = new LinkedList<>();
			for (String link : linksReceived)
				receivedLinks.add(link);

			for (String link : links) {
				if (!receivedLinks.contains(link)) {
					synchronized (PMO_SOUT.class) {
						System.out.println("Odebrano nastepujace linki:");
						for (String llink : linksReceived) {
							System.out.println("> " + llink);
						}
					}
					showErrorAndNotify("Wsrod odebranych linkow nie ma oczkiwanego "
							+ link);
				}
			}
			
			PMO_SOUT.println( "Test linkow dla " + name + " odebrano " + linksReceived.length + " linkow");
		}

		private void removeHalfOfLinks() {
			List<Integer> linksToRemove = new ArrayList<Integer>(
					links.size() / 2 + 1);

			for (int i = 0; i < links.size(); i += 2) {
				linksToRemove.add(linkIDs.get(i)); // kopiowanie ID linkow
			}

			synchronizationByBarrier();

			for (Integer i : linksToRemove) {
				les.linkRemove(id, i);
			}

			for (Integer i : linksToRemove) {
				links.remove(id2link.get(i)); // usuwanie ciagow znakow
				linkIDs.remove(i); // usuwanie identyfikatorow
			}
			synchronizationByBarrier();
		}

		private void publishLinks() {
			linksToPublish = new ArrayList<>(links.size() / 2 + 1);

			for (int i = 0; i < links.size() / 2; i++) {
				linksToPublish.add(linkIDs.get(i)); // dodaje ID linkow do
													// opublikowania
				publicLinks.add(id2link.get(linkIDs.get(i))); // dodaje linki
																// (String) do
																// opublikowanych
			}

			synchronizationByBarrier();

			for (Integer i : linksToPublish) {
				les.publishLink(id, i);
			}

			// czekamy az kazdy opublikuje swoje linki
			synchronizationByBarrier();
		}

		private void removePublicLink() {

			List<Integer> linksToRemove = new ArrayList<>(
					linksToPublish.size() / 2);

			for (int i = 0; i < linksToPublish.size() / 2; i++) {
				linksToRemove.add(linksToPublish.get(i));
			}

			synchronizationByBarrier();

			for (int i = 0; i < linksToRemove.size(); i++) {
				les.linkRemove(id, linksToRemove.get(i));
			}

			for (int i = 0; i < linksToRemove.size(); i++) {
				linksToPublish.remove(linksToRemove.get(i)); // usuwamy ID do
																// opublikowanych
																// linkow
				publicLinks.remove(id2link.get(linksToRemove.get(i))); // usuwamy
																		// linki
																		// (String)
																		// z
																		// opublikowanych
				links.remove(id2link.get(linksToRemove.get(i)));
			}

			synchronizationByBarrier();
		}

		private void prepareAllLinks(Set<String> allLinks) {
			allLinks.clear();
			allLinks.addAll(links);
			allLinks.addAll(publicLinks);
		}

		public void lastTest() {
			int linkToRemoveID = linksToPublish.get(0);

			boolean result = les.linkRemove(id, linkToRemoveID);
			boolean result2 = les.linkRemove(id, linkToRemoveID);

			boolean result3 = true;
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				if (!id2link.containsKey(i)) {
					result3 = les.linkRemove(id, linkToRemoveID);
				}
			}

			if (result != true) {
				showErrorAndNotify("Usuwam istniejacy link; wynikiem linkRemove powinna byc prawda");
			}
			if (result3 == true) {
				showErrorAndNotify("Usuwam link, ktorego nigdy nie bylo");
			}
			if (result2 == true) {
				showErrorAndNotify("Usuwam juz usuniety link, wynikiem linkRemove powinien byc falsz");
			}
		}

		@Override
		public void run() {
			name = Thread.currentThread().getName();
			createLinks();
			try {
				registration();
				System.out.println(" Po rejestracji user " + name + " ma ID : "
						+ id);
				addLinksToSystem();
				testLinks(links);
				removeHalfOfLinks();
				testLinks(links);

				publishLinks();
				Set<String> allLinks = new HashSet<>();
				prepareAllLinks(allLinks);

				testLinks(allLinks, false);

				removePublicLink();
				prepareAllLinks(allLinks);
				testLinks(allLinks, false);

			} catch (InterruptedException | BrokenBarrierException e) {
				System.out.println("Przechwycono wyjatek : " + e.getMessage());
			}
		}

	}

	private class AsynchronicTest implements Runnable {
		@Override
		public void run() {
			List<Thread> tl = new ArrayList<>();
			for (int i = 0; i < THREADS; i++) {
				tl.add(new Thread(new User()));
			}

			for (Thread t : tl) {
				t.setDaemon(true);
				t.start();
			}

			for (Thread t : tl) {
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("Koniec watku testu");
			notifyMainThread();

		}
	}

	public static void main(String[] argv) throws Exception {

		PMO_StartTest test = new PMO_StartTest(argv);

		Thread th = new Thread(test.new AsynchronicTest());
		th.setDaemon(true);
		th.start();

		long t0 = System.currentTimeMillis();
		long tf;
		synchronized (PMO_StartTest.class) {
			PMO_StartTest.class.wait(10000); // maksymalnie 10 sekund / test
			tf = System.currentTimeMillis();
		}
		System.out.println("Koniec pracy testu (watku main)");

		if ( ( tf - t0 ) > 9999) {
			System.out.println( "Wszystko wskazuje na to, ze test nie zakonczyl sie prawidlowo");
			System.out.println( "Czas pracy: " + ( tf - t0 ) );
			return;
		}
		if (test.okFlag.get()) {
			String s = "  _    ___  _  __  _ \n" + " | |  / _ \\| |/ / | |\n"
					+ " | | | | | | ' /  | |\n" + " |_| | |_| | . \\  |_|\n"
					+ " (_)  \\___/|_|\\_\\ (_)\n";

			System.out.println(s);
		}

		System.exit(0);
	}
}
