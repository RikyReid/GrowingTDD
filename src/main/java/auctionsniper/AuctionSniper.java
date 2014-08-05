package auctionsniper;

import util.Announcer;
import auctionsniper.UserRequestListener.Item;

public class AuctionSniper implements AuctionEventListener {

	private final Auction auction;
	private SniperSnapshot snapshot;
	private final Announcer<SniperListener> listeners = Announcer
			.to(SniperListener.class);
	private Item item;

	public AuctionSniper(Auction auction, Item item) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item.identifier);
		this.item = item;
	}

	public void addSniperListener(SniperListener listener) {
		listeners.addListener(listener);
	}

	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch (priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;

			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			}
			else {
				snapshot = snapshot.losing(price);
			}
			
			break;
		}

		notifyChange();
	}

	public SniperSnapshot getSnapshot() {
		return snapshot;
	}

	private void notifyChange() {
		listeners.announce().sniperStateChanged(snapshot);
	}
}
