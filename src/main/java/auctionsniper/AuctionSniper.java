package auctionsniper;

public class AuctionSniper implements AuctionEventListener {

	private final SniperListener sniperListener;
	private final Auction auction;
	private boolean isWinning = false;
	private SniperSnapshot snapshot;

	public AuctionSniper(Auction auction, SniperListener sniperListener, String itemId) {
		this.auction = auction;
		this.sniperListener = sniperListener;
		this.snapshot = SniperSnapshot.joining(itemId);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
//		if (isWinning) {
//			sniperListener.sniperWon();
//		} else {
//			sniperListener.sniperLost();
//		}
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
			break;
		}
		
		notifyChange();
//		isWinning = priceSource == PriceSource.FromSniper;
//
//		if (isWinning) {
//			snapshot = snapshot.winning(price);
//		} else {
//			int bid = price + increment;
//			auction.bid(bid);
//			snapshot = snapshot.bidding(price, bid);
//		}
//		
//		sniperListener.sniperStateChanged(snapshot);		
	}

	private void notifyChange() {
		sniperListener.sniperStateChanged(snapshot);
	}
}
