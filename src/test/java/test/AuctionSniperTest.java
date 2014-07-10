package test;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener.PriceSource;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;

@RunWith(MockitoJUnitRunner.class)
public class AuctionSniperTest {
	@Mock
	private SniperListener sniperListener;
	
	@Mock
	Auction auction;
	
	private AuctionSniper sniper;
	
	@Before
	public void setup() {
		sniper = new AuctionSniper(auction, sniperListener);
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
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		
		verify(auction).bid(price + increment);
		verify(sniperListener, atLeastOnce()).sniperBidding();
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		final int price = 123;
		final int increment = 45;
		
		sniper.currentPrice(price, increment, PriceSource.FromSniper);
		
		verify(sniperListener, atLeastOnce()).sniperWinning();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		final int price = 123;
		final int increment = 45;
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
		
		verify(sniperListener, atLeastOnce()).sniperBidding();
		verify(sniperListener, atLeastOnce()).sniperLost();
	}
	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		final int price = 123;
		final int increment = 45;
		
		sniper.currentPrice(price, increment, PriceSource.FromSniper);
		sniper.auctionClosed();
		
		verify(sniperListener, atLeastOnce()).sniperWinning();
		verify(sniperListener, atLeastOnce()).sniperWon();
	}
}
