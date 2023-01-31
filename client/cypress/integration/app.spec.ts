/// <reference types="cypress" />

beforeEach(() => {
    cy.visit('http://localhost:3000/');
});

it('should load up the expected forms', () => {
    // page has navbar
    cy.get('#mainContainer')
        .find('.navbar')
        .then((found) => found.length)
        .should('equal', 1);

    // page has order form
    cy.get('#mainContainer')
        .find('#orderForm')
        .then((found) => found.length)
        .should('equal', 1);

    // page has order book
    cy.get('#mainContainer')
        .find('#orderBook')
        .then((found) => found.length)
        .should('equal', 1);

    // page has trade history
    cy.get('#mainContainer')
        .find('#tradeHistory')
        .then((found) => found.length)
        .should('equal', 1);
});

it('should load up order and trade information on startup', () => {
    // check orderbook information loaded (table has populated rows)
    cy.get('[data-cy="orderbookTableBody"]')
        .find('tr')
        .then((rows) => rows.length)
        .should('be.greaterThan', 0); // only works if there are orders on the server

    // check trade history information loaded (table has populated rows)
    cy.get('[data-cy="tradeHistoryTableBody"]')
        .find('tr')
        .then((rows) => rows.length)
        .should('be.greaterThan', 0); // only works if there are orders on the server
});

it('should load up order form and private order data on loading account', () => {
    cy.get('[data-cy="accountDropdown"]').click();
    cy.get('.dropdown-menu > :nth-child(1)').click(); // only works if we have hardcoded accounts
    const accountName = 'Gerald'; // I should pull this from the text in the dropDown

    // account name appears on dropdown
    cy.get('[data-cy="accountDropdown"] > #collasible-nav-dropdown').should(
        'have.text',
        accountName
    );

    // form element exists in #orderform div
    cy.get('#orderForm')
        .find('form')
        .then((form) => form.length)
        .should('equal', 1);

    // place order that almost certainly won't be matched and wouldn't exist on the randomised market (so that we can check that it's in the private book)
    const price = '10000000';
    const quantity = '1';
    cy.get('[data-cy="orderFormActionButton_sell"]').click();
    cy.get('[data-cy="orderFormPriceEntry"]').type(price);
    cy.get('[data-cy="orderFormQuantityEntry"]').type(quantity);
    cy.get('[data-cy="orderFormPlaceOrderButton"]').click();

    // private order data is shown [i.e. the unmatched upder placed above appears in private table]
    cy.get('[data-cy="globalPrivateSwitch"]').click();
    cy.get('[data-cy="orderbookTableBody"]')
        .find('tr')
        .contains(price)
        .should('exist');
});

it('should not accept negative inputs to the order form', () => {
    cy.get('[data-cy="accountDropdown"]').click();
    cy.get('.dropdown-menu > :nth-child(1)').click(); // only works if we have hardcoded accounts

    // negative input
    cy.get('[data-cy="orderFormPriceEntry"]').type('-1');
    cy.get('[data-cy="orderFormQuantityEntry"]').type('-1');
    cy.get('[data-cy="orderFormPriceWarning"]').should(
        'have.text',
        'price must be more than 0'
    );
    cy.get('[data-cy="orderFormQuantityWarning"]').should(
        'have.text',
        'quantity must be more than 0'
    );
});

it('should not accept massive inputs to the order form', () => {
    cy.get('[data-cy="accountDropdown"]').click();
    cy.get('.dropdown-menu > :nth-child(1)').click(); // only works if we have hardcoded accounts

    // negative input
    cy.get('[data-cy="orderFormPriceEntry"]').type('100000001');
    cy.get('[data-cy="orderFormQuantityEntry"]').type('100000001');
    cy.get('[data-cy="orderFormPriceWarning"]').should(
        'have.text',
        'price must be at most 100000000'
    );
    cy.get('[data-cy="orderFormQuantityWarning"]').should(
        'have.text',
        'quantity must be at most 100000000'
    );
});

it('should not accept non-numerical inputs to the order form', () => {
    cy.get('[data-cy="accountDropdown"]').click();
    cy.get('.dropdown-menu > :nth-child(1)').click(); // only works if we have hardcoded accounts

    // negative input
    cy.get('[data-cy="orderFormPriceEntry"]').type('Steve!');
    cy.get('[data-cy="orderFormQuantityEntry"]').type('Allan!');

    cy.get('[data-cy="orderFormPriceEntry"]').should('have.text', '');
    cy.get('[data-cy="orderFormQuantityEntry"]').should('have.text', '');
});
