import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { LoginParams, LoginStatus, TAccountState } from "../types/types";
import Api from "./api";

const initialState: TAccountState = {
	username: "",
	token: "",
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
		logout: (state) => {
			state.loginStatus = initialState.loginStatus;
			state.username = initialState.username;
		},
	},
	extraReducers: (builder) => {
		builder.addCase(attemptLogin.fulfilled, (state, action) => {
			console.log("LOGIN SUCCESSFUL");
			state.loginStatus = LoginStatus.ACCEPTED;
			state.username = action.payload.username;
			state.token = action.payload.accessToken;
		});
		builder.addCase(attemptLogin.rejected, (state, action) => {
			console.log("LOGIN FAILED BAD CREDENTIALS");
			state.loginStatus = LoginStatus.REJECTED;
		});
	},
});

export const { logout } = accountSlice.actions;

export default accountSlice.reducer;
