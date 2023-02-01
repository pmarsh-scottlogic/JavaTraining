import React, { useState } from "react";
import { Button, ButtonGroup, Form, ToggleButton } from "react-bootstrap";
import { useAppDispatch, useAppSelector } from "../../app/hooks";
import { createOrder } from "../../app/marketSlice";
import { LoginStatus, OrderParams } from "../../types/types";

import "../tableView.css";

export default function OrderForm({ id }: { id: string }) {
	const [radioValue, setRadioValue] = useState("buy");
	const loginStatus = useAppSelector((state) => state.account.loginStatus);

	const radios = [
		{ name: "Buy", value: "buy" },
		{ name: "Sell", value: "sell" },
	];

	const buySellOptions = (
		<ButtonGroup>
			{radios.map((radio, idx) => (
				<ToggleButton
					key={idx}
					id={`radio-${idx}`}
					type="radio"
					variant={
						idx % 2 === 0 ? "outline-success" : "outline-danger"
					}
					name="radio"
					value={radio.value}
					checked={radioValue === radio.value}
					onChange={(e) => {
						setRadioValue(e.currentTarget.value);
					}}
					data-cy={"orderFormActionButton_" + radio.value}>
					{radio.name}
				</ToggleButton>
			))}
		</ButtonGroup>
	);

	const accessToken = useAppSelector((state) => state.account.token);
	const dispatch = useAppDispatch();
	const onSubmit = () => {
		if (price === -1) setPriceError("please enter a price");
		if (quantity === -1) setQuantityError("please enter a quantity");
		if (price === -1 || quantity === -1) {
			return;
		}

		const params: OrderParams = {
			price: price,
			quantity: quantity,
			action: radioValue,
		};
		dispatch(createOrder(params));
	};

	const [price, setPrice] = useState(-1);
	const [priceError, setPriceError] = useState("");
	const [quantity, setQuantity] = useState(-1);
	const [quantityError, setQuantityError] = useState("");

	function trySetValue(
		valueName: string,
		newValue: number,
		setValue: React.Dispatch<React.SetStateAction<number>>,
		setError: (value: React.SetStateAction<string>) => void
	) {
		if (!newValue) {
			setError("please enter a " + valueName);
			return;
		}

		const minValue = 0;
		if (newValue <= 0) {
			setError(valueName + " must be more than " + minValue.toString());
			return;
		}

		const maxValue = 100000000;
		if (newValue > maxValue) {
			setError(valueName + " must be at most " + maxValue.toString());
			return;
		}

		setError("");
		setValue(newValue);
	}

	const formIfLoggedIn =
		loginStatus === LoginStatus.ACCEPTED ? (
			<Form>
				<div className="centreContent">{buySellOptions}</div>
				<Form.Group className="mb-3" controlId="formPrice">
					<Form.Label>Price</Form.Label>
					<Form.Control
						placeholder="Enter price"
						onChange={(e) =>
							trySetValue(
								"price",
								+e.target.value,
								setPrice,
								setPriceError
							)
						}
						type="number"
						step=".01"
						data-cy="orderFormPriceEntry"
					/>
					<Form.Text
						className="text-danger"
						data-cy="orderFormPriceWarning">
						{priceError}
					</Form.Text>
				</Form.Group>

				<Form.Group className="mb-3" controlId="formQuantity">
					<Form.Label>Quantity</Form.Label>
					<Form.Control
						placeholder="Enter quantity"
						onChange={(e) =>
							trySetValue(
								"quantity",
								+e.target.value,
								setQuantity,
								setQuantityError
							)
						}
						type="number"
						step=".01"
						data-cy="orderFormQuantityEntry"
					/>
					<Form.Text
						className="text-danger"
						data-cy="orderFormQuantityWarning">
						{quantityError}
					</Form.Text>
				</Form.Group>
				<div className="centreContent">
					<Button
						variant="primary"
						onClick={onSubmit}
						data-cy="orderFormPlaceOrderButton">
						Place Order
					</Button>
				</div>
			</Form>
		) : (
			"Log in to place orders"
		);

	return (
		<div id={id}>
			<div className="headerContainer">
				<h4 className="title">Order form</h4>
			</div>
			{formIfLoggedIn}
		</div>
	);
}
