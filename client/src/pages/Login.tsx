import React, { useState } from "react";
import { Button, Form } from "react-bootstrap";

export default function LoginPage() {
	const [username, setUsername] = useState("");
	const [password, setPassword] = useState("");

	const onSubmit = () => {};

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
					/>
				</Form.Group>

				<Form.Group className="mb-3" controlId="password">
					<Form.Label>Quantity</Form.Label>
					<Form.Control
						placeholder="Enter password"
						onChange={(e) => setPassword(e.target.value)}
						data-cy="loginFormPasswordEntry"
					/>
				</Form.Group>

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
