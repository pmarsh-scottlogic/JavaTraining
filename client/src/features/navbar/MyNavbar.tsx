import React from 'react';

import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { hardAccounts } from '../../testUtils/hardcodedAccounts';
import { logout, setAccount } from '../../app/accountSlice';
import logo from '../../branding/logoPure.svg';
import { Account } from '../../types/types';

//https://react-bootstrap.github.io/components/navbar/

export default function MyNavbar() {
    const dispatch = useAppDispatch();
    const accountDropdownName = useAppSelector((state) =>
        state.account.loggedIn ? state.account.name : 'Log in'
    );

    const handleAccountChange = function (account: Account) {
        dispatch(setAccount(account));
    };

    const accountOptions = hardAccounts.map((account, i) => {
        return (
            <NavDropdown.Item
                key={i}
                onClick={() => {
                    handleAccountChange(account);
                }}
            >
                {account.name}
            </NavDropdown.Item>
        );
    });

    return (
        <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
            <Container>
                <Navbar.Brand href="#home">
                    <img
                        alt=""
                        src={logo}
                        height="30"
                        className="d-inline-block align-top"
                    />{' '}
                    Stonks
                </Navbar.Brand>
                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav">
                    <Nav className="me-auto"></Nav>
                    <Nav>
                        <NavDropdown
                            title={accountDropdownName}
                            id="collasible-nav-dropdown"
                            data-cy="accountDropdown"
                        >
                            {accountOptions}
                            <NavDropdown.Divider />
                            <NavDropdown.Item
                                key={-1}
                                onClick={() => {
                                    dispatch(logout());
                                }}
                            >
                                Logout
                            </NavDropdown.Item>
                        </NavDropdown>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}
