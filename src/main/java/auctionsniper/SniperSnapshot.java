package auctionsniper;

public class SniperSnapshot {
	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState state;
	
	public SniperSnapshot(String itemId, int lastPrice, int lastBid, SniperState state) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.state = state;
	}

	public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
		return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
	}

	public SniperSnapshot winning(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
	}

	public SniperSnapshot closed() {
		return new SniperSnapshot(itemId, lastPrice, lastBid, state.whenAuctionedClosed());
	}

	public static SniperSnapshot joining(String itemId) {
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}
	
	public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
		return itemId.equals(sniperSnapshot.itemId);
	}
	
	@Override
	public String toString() {
		return "SniperState [itemId=" + itemId + ", lastPrice=" + lastPrice
				+ ", lastBid=" + lastBid + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + lastBid;
		result = prime * result + lastPrice;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SniperSnapshot other = (SniperSnapshot) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (lastBid != other.lastBid)
			return false;
		if (lastPrice != other.lastPrice)
			return false;
		return true;
	}

	public SniperSnapshot losing(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
	}
}

