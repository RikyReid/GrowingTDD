package auctionsniper;

import util.Defect;

public enum SniperState {
	JOINING {
		@Override
		public SniperState whenAuctionedClosed() {
			return LOST;
		}
	},
	BIDDING {
		@Override
		public SniperState whenAuctionedClosed() {
			return LOST;
		}

	},
	WINNING {
		@Override
		public SniperState whenAuctionedClosed() {
			return WON;
		}

	},
	LOSING {
		@Override
		public SniperState whenAuctionedClosed() {
			return LOST;
		}		
	}, LOST, WON;

	public SniperState whenAuctionedClosed() {
		throw new Defect("Auction is already closed");
	}
}
