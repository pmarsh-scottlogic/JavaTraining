import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { OrderParams, TMarketState } from "../types/types";
import Api from "./api";

const BASE_URL = "http://localhost:8080"; // ideally this would come from an environment variable

const initialState: TMarketState = {
	orderbookBuy: [],
	orderbookSell: [],
	privateOrderbookBuy: [],
	privateOrderbookSell: [],
	orderDepthBuy: [],
	orderDepthSell: [],
	tradeHistory: [],
};

const api = new Api(BASE_URL);

export const fetchBuyOrders = createAsyncThunk(
	"orderbook/getBuys",
	async () => {
		return api.getPublicBuyOrders();
	}
);

export const fetchSellOrders = createAsyncThunk(
	"orderbook/getSells",
	async () => {
		return api.getPublicSellOrders();
	}
);

export const fetchPrivateBuyOrders = createAsyncThunk(
	"orderbook/getPrivateBuys",
	async (token: string) => {
		return api.getPrivateBuyOrders(token);
	}
);

export const fetchPrivateSellOrders = createAsyncThunk(
	"orderbook/getPrivateSells",
	async (token: string) => {
		return api.getPrivateSellOrders(token);
	}
);

export const fetchBuyOrderDepth = createAsyncThunk(
	"orderbook/depth/getBuys",
	async () => {
		return api.getBuyOrderDepth();
	}
);

export const fetchSellOrderDepth = createAsyncThunk(
	"orderbook/depth/getSells",
	async () => {
		return api.getSellOrderDepth();
	}
);

export const fetchTradeHistory = createAsyncThunk(
	"tradeHistory/get",
	async () => {
		return api.getTradeHistory();
	}
);

export const createOrder = createAsyncThunk(
	"createOrder",
	async ({
		orderParams,
		accessToken,
	}: {
		orderParams: OrderParams;
		accessToken: string;
	}) => {
		return api.createOrder(orderParams, accessToken);
	}
);

export const marketSlice = createSlice({
	name: "market",
	initialState,
	reducers: {
		// no auto generated reducers, we just want to add in some extra reducers that work on the thunks we've created above
	},
	extraReducers: (builder) => {
		builder.addCase(fetchBuyOrders.fulfilled, (state, action) => {
			state.orderbookBuy = action.payload;
		});
		builder.addCase(fetchSellOrders.fulfilled, (state, action) => {
			state.orderbookSell = action.payload;
		});
		builder.addCase(fetchPrivateBuyOrders.fulfilled, (state, action) => {
			state.privateOrderbookBuy = action.payload;
		});
		builder.addCase(fetchPrivateSellOrders.fulfilled, (state, action) => {
			state.privateOrderbookSell = action.payload;
		});
		builder.addCase(fetchBuyOrderDepth.fulfilled, (state, action) => {
			state.orderDepthBuy = action.payload;
		});
		builder.addCase(fetchSellOrderDepth.fulfilled, (state, action) => {
			state.orderDepthSell = action.payload;
		});
		builder.addCase(fetchTradeHistory.fulfilled, (state, action) => {
			state.tradeHistory = action.payload;
		});
		builder.addCase(createOrder.fulfilled, (state, action) => {
			state.orderbookBuy = action.payload.buy;
			state.orderbookSell = action.payload.sell;
			state.privateOrderbookBuy = action.payload.buyPrivate;
			state.privateOrderbookSell = action.payload.sellPrivate;
			state.tradeHistory = action.payload.history;
			state.orderDepthBuy = action.payload.orderDepthBuy;
			state.orderDepthSell = action.payload.orderDepthSell;
		});
		// we could also handle "pending" or "rejected" for each of the thunks, either for a loading indicator, or error information
	},
});

export default marketSlice.reducer;
