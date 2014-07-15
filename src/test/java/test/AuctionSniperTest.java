package test;

import static org.junit.Assert.*;
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

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {
	private static final String ITEMID = "1";
	
	@Mock
	private SniperListener sniperListener;
	
	@Mock
	Auction auction;
	
	private AuctionSniper sniper;
	
	@Before
	public void setup() {
		sniper = new AuctionSniper(auction, sniperListener, ITEMID);
	}

	@Test
	public void reportsLostWhenAuctionClosesImmediately() {
		sniper.auctionClosed();
		verify(sniperListener).sniperLost();
	}

	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		verify(auction).bid(price + increment);
		
		ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor.forClass(SniperSnapshot.class);
		verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
		assertEquals(SniperState.BIDDING, argument.getValue().state);
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);

		ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor.forClass(SniperSnapshot.class);
		verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
		assertEquals(SniperState.BIDDING, argument.getValue().state);

		sniper.currentPrice(135, 45, PriceSource.FromSniper);

		verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
		assertEquals(SniperState.WINNING, argument.getValue().state);
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		final int price = 123;
		final int increment = 45;
		final int bid = price + increment;
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
		
		ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor.forClass(SniperSnapshot.class);
		verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
		assertEquals(SniperState.BIDDING, argument.getValue().state);
		verify(sniperListener, atLeastOnce()).sniperLost();
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final int price = 123;
		final int increment = 45;
		
		sniper.currentPrice(price, increment, PriceSource.FromSniper);

		ArgumentCaptor<SniperSnapshot> argument = ArgumentCaptor.forClass(SniperSnapshot.class);
		verify(sniperListener, atLeastOnce()).sniperStateChanged(argument.capture());
		assertEquals(SniperState.WINNING, argument.getValue().state);

		sniper.auctionClosed();
		verify(sniperListener, atLeastOnce()).sniperWon();
	}
}
