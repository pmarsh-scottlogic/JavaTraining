import React, { useState } from "react";
import Table from "react-bootstrap/Table";
import { LoginStatus, TOrderbook } from "../../types/types";
import { MDBSwitch } from "mdb-react-ui-kit";
import "../tableView.css";
import { useAppSelector } from "../../app/hooks";

interface OrderbookProps {
	buyOrderBook: TOrderbook;
	sellOrderBook: TOrderbook;
	privateBuyOrderBook: TOrderbook;
	privateSellOrderBook: TOrderbook;
	id: string;
}

function makeRows(buy: TOrderbook, sell: TOrderbook) {
	const numToShow = Math.max(buy.length, sell.length);

	const rows = [];
	for (let i = 0; i < numToShow; i++) {
		rows.push(
			<tr key={i}>
				<td>{buy[i] ? buy[i].quantity.toFixed(2) : "-"}</td>
				<td>{buy[i] ? "£" + buy[i]?.price.toFixed(2) : "-"}</td>
				<td>{sell[i] ? "£" + sell[i]?.price.toFixed(2) : "-"}</td>
				<td>{sell[i] ? sell[i].quantity.toFixed(2) : "-"}</td>
			</tr>
		);
	}

	return rows;
}

export default function Orderbook({
	buyOrderBook,
	sellOrderBook,
	privateBuyOrderBook,
	privateSellOrderBook,
	id,
}: OrderbookProps) {
	const loginStatus = useAppSelector((state) => state.account.loginStatus);
	const [showPrivate, setShowPrivate] = useState(false);

	const headers = (
		<tr>
			<th>size</th>
			<th>bid</th>
			<th>ask</th>
			<th>size</th>
		</tr>
	);

	return (
		<div className="tableView" id={id}>
			<div className="headerContainer">
				<h4 className="title">Orderbook</h4>
				<div className="switchContainer">
					{showPrivate ? "private" : "global"}
					<div style={{ paddingLeft: "1em" }}></div>
					<MDBSwitch
						id="flexSwitchCheckDefault"
						onChange={() => setShowPrivate(!showPrivate)}
						data-cy="globalPrivateSwitch"
					/>
				</div>
			</div>

			<div className="tableContainer">
				<Table borderless hover size="sm" className="no-margin">
					<thead>{headers}</thead>
					<tbody data-cy="orderbookTableBody">
						{showPrivate ? (
							loginStatus === LoginStatus.ACCEPTED ? (
								makeRows(
									privateBuyOrderBook,
									privateSellOrderBook
								)
							) : (
								<td style={{ columnSpan: "all" }}>
									login to see private orders
								</td>
							)
						) : (
							makeRows(buyOrderBook, sellOrderBook)
						)}
					</tbody>
				</Table>
			</div>
		</div>
	);
}
