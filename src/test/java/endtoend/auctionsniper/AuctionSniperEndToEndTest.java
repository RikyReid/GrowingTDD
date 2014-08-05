package endtoend.auctionsniper;

import java.util.concurrent.CountDownLatch;

import org.junit.After;
import org.junit.Test;

import xmpp.XMPPAuction;
import auctionsniper.Auction;

public class AuctionSniperEndToEndTest {
	private final FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
	private final FakeAuctionServer auctionServer2 = new FakeAuctionServer("item-65432");
	
	private final ApplicationRunner application = new ApplicationRunner();
	
	@Test
	public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
		auctionServer.startSellingItem();
		application.startBiddingIn(auctionServer);
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.announceClosed();
		application.showsSniperHasLostAuction(auctionServer, 0, 0);
	}

	@Test
	public void sniperMakesAHigherBidButLoses() throws Exception {
		auctionServer.startSellingItem();
		application.startBiddingIn(auctionServer);
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auctionServer.reportPrice(1000, 98, "other bid");
		application.hasShownSniperIsBidding(auctionServer, 1000, 1098);
		
		auctionServer.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auctionServer.announceClosed();
		application.showsSniperHasLostAuction(auctionServer, 1000, 1098);
	}

	@Test
	public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
		auctionServer.startSellingItem();
		application.startBiddingIn(auctionServer);
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auctionServer.reportPrice(1000, 98, "other bid");
		application.hasShownSniperIsBidding(auctionServer, 1000, 1098);
		
		auctionServer.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auctionServer.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSniperIsWinning(auctionServer, 1098);

		auctionServer.announceClosed();
		application.showsSniperHasWonAuction(auctionServer, 1098);
	}

	@Test
	public void sniperBidsForMultipleItems() throws Exception {
		auctionServer.startSellingItem();
		auctionServer2.startSellingItem();
		
		application.startBiddingIn(auctionServer, auctionServer2);
		
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auctionServer.reportPrice(1000, 98, "other bidber");
//		application.hasShownSniperIsBidding(auction, 1000, 1098);
		auctionServer.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auctionServer2.reportPrice(500, 21, "other bidder");		
		auctionServer2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

		auctionServer.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);		
		auctionServer2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);		

		application.hasShownSniperIsWinning(auctionServer, 1098);
		application.hasShownSniperIsWinning(auctionServer2, 521);
		
		auctionServer.announceClosed();
		auctionServer2.announceClosed();
		
		application.showsSniperHasWonAuction(auctionServer, 1098);
		application.showsSniperHasWonAuction(auctionServer2, 521);
	}
	
	@Test
	public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
		auctionServer.startSellingItem();
		application.startBiddingWithStopPrice(auctionServer, 1100);
		auctionServer.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auctionServer.reportPrice(1000, 98, "other bidder");
		application.hasShownSniperIsBidding(auctionServer, 1000, 1098);
		
		auctionServer.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auctionServer.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auctionServer, 1197, 1098);

		auctionServer.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auctionServer, 1207, 1098);
		
		auctionServer.announceClosed();
		application.showsSniperHasLostAuction(auctionServer, 1207, 1098);
	}
	
	@After
	public void stopApplication() {
		application.stop();
	}
	
	@After
	public void stopAuction() {
		auctionServer.stop();
	}

}
