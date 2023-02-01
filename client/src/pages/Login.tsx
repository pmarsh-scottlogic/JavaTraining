import React, { useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { attemptLogin } from "../app/accountSlice";
import { useAppDispatch, useAppSelector } from "../app/hooks";
import { LoginStatus } from "../types/types";

export default function LoginPage() {
	const loginStatus = useAppSelector((state) => state.account.loginStatus);
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");
	const dispatch = useAppDispatch();

	const onSubmit = () => {
		dispatch(attemptLogin({ username, password }));
		setUsername("");
		setPassword("");
	};

	const navigate = useNavigate();

	useEffect(() => {
		if (loginStatus === LoginStatus.ACCEPTED) {
			navigate("/");
		}
	}, [loginStatus, navigate]);

	return (
		<div
			style={{
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
				height: "100%",
			}}>
			<Form>
				<Form.Group className="mb-3" controlId="username">
					<Form.Label>Username</Form.Label>
					<Form.Control
						placeholder="Enter username"
						onChange={(e) => setUsername(e.target.value)}
						data-cy="loginFormUsernameEntry"
						value={username}
					/>
				</Form.Group>

				<Form.Group className="mb-3" controlId="password">
					<Form.Label>Password</Form.Label>
					<Form.Control
						type="password"
						placeholder="Enter password"
						onChange={(e) => setPassword(e.target.value)}
						data-cy="loginFormPasswordEntry"
						value={password}
					/>
				</Form.Group>

				<Form.Text
					id="txtCredentialsWarning"
					className="text-danger"
					data-cy="loginFormCredentialsWarning">
					{loginStatus === LoginStatus.REJECTED
						? "There are no accounts with that username and password"
						: ""}
				</Form.Text>

				<div className="centreContent">
					<Button
						variant="primary"
						onClick={onSubmit}
						data-cy="loginFormLoginButton">
						Log in
					</Button>
				</div>
			</Form>
		</div>
	);
}
