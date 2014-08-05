package test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.UserRequestListener.Item;

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {
	protected static final String ITEM_ID = "item-id";
	public static final Item ITEM = new Item(ITEM_ID, 1234);

	@Mock
	private SniperListener sniperListener;

	@Mock
	Auction auction;

	private AuctionSniper sniper;

	@Before
	public void setup() {
		sniper = new AuctionSniper(auction, ITEM);
		sniper.addSniperListener(sniperListener);
	}

	@Test
	public void reportsLostWhenAuctionClosesImmediately() {
		sniper.auctionClosed();
		expectedSniperState(SniperState.LOST);
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

		verify(auction).bid(bid);

		expectedToBeBidding();
	}

	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);

		expectedToBeBidding();
		sniper.currentPrice(135, 45, PriceSource.FromSniper);

		expectedToBeWinning();
	}

	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		final int price = 123;
		final int increment = 45;

		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);

		expectedToBeBidding();

		sniper.auctionClosed();

		expectedToHaveLost();
	}

	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final int price = 123;
		final int increment = 45;

		sniper.currentPrice(price, increment, PriceSource.FromSniper);

		expectedToBeWinning();

		sniper.auctionClosed();
		expectedToHaveWon();
	}

	private void expectedToBeBidding() {
		expectedSniperState(SniperState.BIDDING);
	}

	private void expectedToBeWinning() {
		expectedSniperState(SniperState.WINNING);
	}

	private void expectedToHaveWon() {
		expectedSniperState(SniperState.WON);
	}

	private void expectedToHaveLost() {
		expectedSniperState(SniperState.LOST);
	}

	private void expectedSniperState(SniperState state) {
		ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor
				.forClass(SniperSnapshot.class);
		verify(sniperListener, atLeastOnce()).sniperStateChanged(
				argument.capture());
		assertEquals(state, argument.getValue().state);
	}

}
