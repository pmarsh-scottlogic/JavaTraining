import { configureStore, ThunkAction, Action } from '@reduxjs/toolkit';
import accountSlice from './accountSlice';
import marketReducer from './marketSlice';

export const store = configureStore({
    reducer: {
        market: marketReducer,
        account: accountSlice,
    },
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
export type AppThunk<ReturnType = void> = ThunkAction<
    ReturnType,
    RootState,
    unknown,
    Action<string>
>;
