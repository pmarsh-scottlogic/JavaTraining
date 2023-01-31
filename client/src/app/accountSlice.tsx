import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import {
	Account,
	LoginParams,
	LoginStatus,
	TAccountState,
} from "../types/types";
import Api from "./api";

const DEFAULT_ACCOUNT_INFO = "na";

const initialState: TAccountState = {
	loggedIn: false,
	name: DEFAULT_ACCOUNT_INFO,
	username: DEFAULT_ACCOUNT_INFO,
	loginStatus: LoginStatus.UNATTEMPTED,
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
		},
		logout: (state) => {
			state.loggedIn = initialState.loggedIn;
			state.name = initialState.name;
		},
	},
	extraReducers: (builder) => {
		builder.addCase(attemptLogin.fulfilled, (state, action) => {
			console.log(action.payload);
			state.loginStatus = LoginStatus.ACCEPTED;
		});
		builder.addCase(attemptLogin.rejected, (state, action) => {
			console.log("LOGIN FAILED BAD CREDENTIALS");
			state.loginStatus = LoginStatus.REJECTED;
		});
	},
});

export const { setAccount, logout } = accountSlice.actions;

export default accountSlice.reducer;
