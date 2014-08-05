package integration.auctionsniper.xmpp;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.XMPPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import xmpp.XMPPAuction;
import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import endtoend.auctionsniper.ApplicationRunner;
import endtoend.auctionsniper.FakeAuctionServer;

public class XMPPAuctionHouseTest {
	private final FakeAuctionServer auctionServer = new FakeAuctionServer(
			"item-54321");
	private XMPPConnection connection;


	@Before
	public void openConnection() throws Exception {
		connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
		connection.connect();
		connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, "Auction");
		auctionServer.startSellingItem();
	}

	@After
	public void closeConnection() {
		if (connection != null) {
			connection.disconnect();
		}
	}

	@Test
	public void receivesEventsFromAucitonServerAfterJoining() throws Exception {
		CountDownLatch auctionWasClosed = new CountDownLatch(1);

		Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
		auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));
		
		auction.join();
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
	}
	
	private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
		return new AuctionEventListener() {
			@Override
			public void auctionClosed() {
				auctionWasClosed.countDown();
				
			}@Override
			public void currentPrice(int price, int increment,
					PriceSource priceSource) {
				// not implemented
			}
		};
	}
}
