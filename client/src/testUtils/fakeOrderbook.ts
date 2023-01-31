import { faker } from '@faker-js/faker';

interface OrderBookItem {
    price: number;
    quantity: number;
}

export function fakeOrderbookItem(): OrderBookItem {
    return {
        price: faker.datatype.number({
            min: 1,
            max: 10,
            precision: 0.1,
        }),
        quantity: faker.datatype.number({
            min: 10,
            max: 99,
            precision: 0.01,
        }),
    };
}

export function fakeOrderbookItems(n: number) {
    const fakeOrderbookItems: Array<OrderBookItem> = [];
    for (let i = 0; i < n; i++) {
        fakeOrderbookItems.push(fakeOrderbookItem());
    }
    return fakeOrderbookItems;
}
