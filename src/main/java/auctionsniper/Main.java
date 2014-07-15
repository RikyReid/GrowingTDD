package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import xmpp.XMPPAuction;
import auctionsniper.ui.MainWindow;

public class Main {
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;

	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/"
			+ AUCTION_RESOURCE;
	
	public static final String BIDDING = "Bidding";
	public static final String JOINING = "Joining";
	public static final String WINNING = "Winning";
	public static final String LOST = "Lost";
	public static final String WON = "Won";


	private MainWindow ui;
	private Chat notToBEGCd;

	public Main() throws Exception {
		startUserInterface();
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		main.joinAuction(
				connection(args[ARG_HOSTNAME], args[ARG_USERNAME],
						args[ARG_PASSWORD]), args[ARG_ITEM_ID]);
	}

	private static XMPPConnection connection(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);

		return connection;
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId,
				connection.getServiceName());
	}

	private void startUserInterface() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				ui = new MainWindow();
			}
		});
	}

	private void joinAuction(XMPPConnection connection, String itemId)
			throws XMPPException {
		disconnectWhenUICloses(connection);
		final Chat chat = connection.getChatManager().createChat(
				auctionId(itemId, connection), null);
		this.notToBEGCd = chat;

		Auction auction = new XMPPAuction(chat);
		chat.addMessageListener(new AuctionMessageTranslator(connection
				.getUser(),
				new AuctionSniper(auction, new SniperStateDisplay(), itemId)));
		auction.join();
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
			}
		});
	}
	
	public class SniperStateDisplay implements SniperListener {

		public void sniperLost() {
			showStatus(LOST);
		}

		@Override
		public void sniperStateChanged(final SniperSnapshot state) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ui.sniperStatusChanged(state, Main.BIDDING);
				}
			});

			showStatus(BIDDING);
		}

		private void showStatus(final String status) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ui.showStatus(status);
				}
			});
		}

		@Override
		public void sniperWon() {
			showStatus(WON);
		}
	}
}
