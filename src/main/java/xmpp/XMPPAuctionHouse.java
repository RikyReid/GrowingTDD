package xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.UserRequestListener.Item;

public class XMPPAuctionHouse implements AuctionHouse {
	private static final String LOGGER_NAME = "auction-sniper";
	public static final String LOG_FILE_NAME = "auction-sniper.log";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/"
			+ XMPPAuctionHouse.AUCTION_RESOURCE;
	public static final String AUCTION_RESOURCE = "Auction";

	private final XMPPConnection connection;
	
	public XMPPAuctionHouse(XMPPConnection connection)
			throws Exception {
		this.connection = connection;
//		this.failureReporter = new LoggingXMPPFailureReporter(makeLogger());
	}

	@Override
	public Auction auctionFor(Item item) {
		return new XMPPAuction(connection, item.identifier);
	}

	public void disconnect() {
		connection.disconnect();
	}
	
	public static XMPPAuctionHouse connect(String hostname, String username,
			String password) throws Exception {
		XMPPConnection connection = new XMPPConnection(hostname);
		try {
			connection.connect();
			connection.login(username, password, AUCTION_RESOURCE);
			return new XMPPAuctionHouse(connection);
		} catch (XMPPException xmppe) {
			throw new XMPPException("Could not connect to auction: "
					+ connection, xmppe);
		}
	}
}
