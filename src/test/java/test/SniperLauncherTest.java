package test;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperCollector;
import auctionsniper.SniperLauncher;
import auctionsniper.UserRequestListener.Item;

@RunWith(MockitoJUnitRunner.class)
public class SniperLauncherTest {

	@Mock
	Auction auction;

	@Mock
	AuctionHouse auctionHouse;

	@Mock
	SniperCollector sniperCollector;

	private SniperLauncher launcher;

	@Before
	public void setup() {
		 launcher = new SniperLauncher(auctionHouse,
					sniperCollector);
	}
	
	@Test
	public void addNewSniperToCollectorAndThenJoinsAuction() {
		final Item item = new Item("item 123", 456);

		when(auctionHouse.auctionFor(item)).thenReturn(auction);
		launcher.joinAuction(item);

		verify(auction).addAuctionEventListener(argThat(sniperForItem(item)));
		verify(sniperCollector).addSniper(argThat(sniperForItem(item)));
	}

	protected Matcher<AuctionSniper> sniperForItem(Item item) {
		return new FeatureMatcher<AuctionSniper, String>(
				equalTo(item.identifier), "sniper with item id", "item") {
			@Override
			protected String featureValueOf(AuctionSniper actual) {
				return actual.getSnapshot().itemId;
			}
		};
	}
}
