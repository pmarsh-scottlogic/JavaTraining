import React from "react";

import Container from "react-bootstrap/Container";
import Nav from "react-bootstrap/Nav";
import Navbar from "react-bootstrap/Navbar";
import NavDropdown from "react-bootstrap/NavDropdown";
import { useAppDispatch, useAppSelector } from "../../app/hooks";
import { hardAccounts } from "../../testUtils/hardcodedAccounts";
import { logout, setAccount } from "../../app/accountSlice";
import logo from "../../branding/logoPure.svg";
import { Account, LoginStatus } from "../../types/types";
import { stdout } from "process";

//https://react-bootstrap.github.io/components/navbar/

export default function MyNavbar() {
	const dispatch = useAppDispatch();
	const loginStatus = useAppSelector((state) => state.account.loginStatus);
	const username = useAppSelector((state) => state.account.username);

	const accountSection =
		loginStatus === LoginStatus.ACCEPTED ? (
			<NavDropdown
				title={username}
				id="collasible-nav-dropdown"
				data-cy="accountDropdown">
				<NavDropdown.Divider />
				<NavDropdown.Item
					key={-1}
					onClick={() => {
						dispatch(logout());
					}}>
					Logout
				</NavDropdown.Item>
			</NavDropdown>
		) : (
			<Nav>
				<a href="/login">
					<button
						className="btn btn-dark"
						style={{ color: "#888888" }}>
						Log in
					</button>
				</a>
				<a href="/signup">
					<button
						className="btn btn-dark"
						style={{ color: "#888888" }}>
						Sign up
					</button>
				</a>
			</Nav>
		);

	return (
		<Navbar collapseOnSelect expand="lg" bg="dark" variant="dark">
			<Container>
				<Navbar.Brand href="/">
					<img
						alt=""
						src={logo}
						height="30"
						className="d-inline-block align-top"
					/>{" "}
					Stonks
				</Navbar.Brand>
				<Navbar.Toggle aria-controls="responsive-navbar-nav" />
				<Navbar.Collapse id="responsive-navbar-nav">
					<Nav className="me-auto"></Nav>
					{accountSection}
				</Navbar.Collapse>
			</Container>
		</Navbar>
	);
}
