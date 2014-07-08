package auctionsniper;

public interface SniperListener extends AuctionEventListener {
	void sniperLost();
	void sniperBidding();
}
