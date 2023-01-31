import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { Account, TAccountState } from '../types/types';

const DEFAULT_ACCOUNT_INFO = 'na';

const initialState: TAccountState = {
    loggedIn: false,
    name: DEFAULT_ACCOUNT_INFO,
    id: DEFAULT_ACCOUNT_INFO,
};

export const accountSlice = createSlice({
    name: 'account',
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
});

export const { setAccount, logout } = accountSlice.actions;

export default accountSlice.reducer;
