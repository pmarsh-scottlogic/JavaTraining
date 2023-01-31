import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Account, LoginParams, TAccountState } from "../types/types";
import Api from "./api";

const DEFAULT_ACCOUNT_INFO = "na";

const initialState: TAccountState = {
	loggedIn: false,
	name: DEFAULT_ACCOUNT_INFO,
	id: DEFAULT_ACCOUNT_INFO,
};

const BASE_URL = "http://localhost:8080"; // ideally this would come from an environment variable
const api = new Api(BASE_URL);

export const attemptLogin = createAsyncThunk(
	"attemptLogin",
	async (loginParams: LoginParams) => {
		return api.attemptLogin(loginParams);
	}
);

export const accountSlice = createSlice({
	name: "account",
	initialState,
	reducers: {
		setAccount: (state, action: PayloadAction<Account>) => {
			state.loggedIn = true;
			state.name = action.payload.name;
			state.id = action.payload.id;
		},
		logout: (state) => {
			state.loggedIn = initialState.loggedIn;
			state.name = initialState.name;
			state.id = initialState.id;
		},
	},
	extraReducers: (builder) => {
		builder.addCase(attemptLogin.fulfilled, (state, action) => {
			console.log(action.payload);
		});
	},
});

export const { setAccount, logout } = accountSlice.actions;

export default accountSlice.reducer;
