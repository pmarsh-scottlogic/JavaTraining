// This class is analogous to the "userAPI" in the createAsyncThunks docs, and basically encapsulates API calls

import { LoginParams, OrderParams } from "../types/types";

// We avoid hard coding URL here (although I've just hard coded it elsewhere...) so we don't have to change the code if we deploy to different environments
export default class Api {
	baseUrl: string;

	constructor(baseUrl: string) {
		this.baseUrl = baseUrl;
	}

	async get(url: string) {
		const response = await fetch(url);
		return response.json();
	}

	async getPublicBuyOrders() {
		return await this.get(this.baseUrl + "/public/orderbook/buy");
	}

	async getPrivateBuyOrders() {
		return await this.get(this.baseUrl + "/private/orderbook/buy");
	}

	async getPublicSellOrders() {
		return await this.get(this.baseUrl + "/public/orderbook/sell");
	}

	async getPrivateSellOrders() {
		return await this.get(this.baseUrl + "/private/orderbook/sell");
	}

	async getBuyOrderDepth() {
		return await this.get(this.baseUrl + "/public/orderbook/depth/buy");
	}

	async getSellOrderDepth() {
		return await this.get(this.baseUrl + "/public/orderbook/depth/sell");
	}

	async getTradeHistory() {
		return await this.get(this.baseUrl + "/public/tradebook/");
	}

	async createOrder(params: OrderParams) {
		const url = this.baseUrl + "/make/order/";
		const response = await fetch(url, {
			method: "POST",
			mode: "cors",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(params),
		});
		return response.json();
	}

	async attemptLogin(params: LoginParams) {
		const url = this.baseUrl + "/auth/login";
		const response = await fetch(url, {
			method: "POST",
			mode: "cors",
			headers: {
				"Content-Type": "application/json",
			},
			body: JSON.stringify(params),
		});
		return response.json();
	}
}
