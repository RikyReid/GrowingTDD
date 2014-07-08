package endtoend.auctionsniper;

import static endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;
import auctionsniper.Main;
import auctionsniper.SniperState;

public class ApplicationRunner {
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";
	private AuctionSniperDriver driver;

	public void startBiddingIn(final FakeAuctionServer auction) {
		Thread thread = new Thread("Test Application") {
			public void run() {
				try {
					Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD,
							auction.getItemId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showsSniperStatus(SniperState.JOINING.toString());
	}
	
	public void showsSniperHasLostAuction() {
		driver.showsSniperStatus(SniperState.LOST.toString());			
	}

	public void hasShownSniperIsBidding() {
		driver.showsSniperStatus(SniperState.BIDDING.toString());
	}
	
	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
	}
}