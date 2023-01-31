import React, { useEffect } from "react";
import Orderbook from "../features/orderbook/Orderbook";
import TradeHistory from "../features/tradehistory/Tradehistory";
import { useAppDispatch, useAppSelector } from "../app/hooks";
import {
	fetchBuyOrderDepth,
	fetchBuyOrders,
	fetchPrivateBuyOrders,
	fetchPrivateSellOrders,
	fetchSellOrderDepth,
	fetchSellOrders,
	fetchTradeHistory,
} from "../app/marketSlice";
import "../app.css";
import MyNavbar from "../features/navbar/MyNavbar";
import OrderForm from "../features/orderForm/OrderForm";
import MarketDepth from "../features/MarketDepth/MarketDepth";

export default function Home() {
	const buyOrderBook = useAppSelector((state) => state.market.orderbookBuy);
	const sellOrderBook = useAppSelector((state) => state.market.orderbookSell);
	const privateBuyOrderBook = useAppSelector(
		(state) => state.market.privateOrderbookBuy
	);
	const privateSellOrderBook = useAppSelector(
		(state) => state.market.privateOrderbookSell
	);
	const buyOrderDepth = useAppSelector((state) => state.market.orderDepthBuy);
	const sellOrderDepth = useAppSelector(
		(state) => state.market.orderDepthSell
	);
	const tradeHistory = useAppSelector((state) => state.market.tradeHistory);
	const loggedIn = useAppSelector((state) => state.account.loggedIn);
	const selectedAccountId = useAppSelector((state) => state.account.id);
	// is it better paactise to put these selectors in a separate file somewhere? Maybe with the relevant slice?

	const dispatch = useAppDispatch();

	useEffect(() => {
		dispatch(fetchBuyOrders());
		dispatch(fetchSellOrders());
		dispatch(fetchBuyOrderDepth());
		dispatch(fetchSellOrderDepth());

		dispatch(fetchTradeHistory());
	}, [dispatch]);

	useEffect(() => {
		if (loggedIn) {
			dispatch(fetchPrivateBuyOrders(selectedAccountId));
			dispatch(fetchPrivateSellOrders(selectedAccountId));
		}
	}, [dispatch, loggedIn, selectedAccountId]);

	return (
		<div id="gridContainer">
			<OrderForm id="orderForm" />
			<Orderbook
				id="orderBook"
				buyOrderBook={buyOrderBook}
				sellOrderBook={sellOrderBook}
				privateBuyOrderBook={privateBuyOrderBook}
				privateSellOrderBook={privateSellOrderBook}
			/>
			<TradeHistory id="tradeHistory" tradeHistory={tradeHistory} />
			<MarketDepth
				id="marketDepth"
				buyDepthData={buyOrderDepth}
				sellDepthData={sellOrderDepth}
			/>
		</div>
	);
}
