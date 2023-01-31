export type OrderbookItem = {
	quantity: number;
	price: number;
};

export type TOrderbook = Array<OrderbookItem>;

export type Trade = {
	id: string;
	accountBuyer: string;
	orderBuy: string;
	accountSeller: string;
	orderSell: string;
	price: number;
	quantity: number;
	datetime: number;
};

export type TMarketState = {
	orderbookBuy: Array<OrderbookItem>;
	orderbookSell: Array<OrderbookItem>;
	privateOrderbookBuy: Array<OrderbookItem>;
	privateOrderbookSell: Array<OrderbookItem>;
	orderDepthBuy: Array<OrderbookItem>;
	orderDepthSell: Array<OrderbookItem>;
	tradeHistory: Array<Trade>;
};

export type TAccountState = {
	loggedIn: boolean;
	name: string;
	id: string;
};
export type Account = {
	name: string;
	id: string;
};

export type OrderParams = {
	account: string;
	price: number;
	quantity: number;
	action: string;
};

export type LoginParams = {
	username: string;
	password: string;
};
