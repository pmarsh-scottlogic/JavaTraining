import React from "react";
import MyNavbar from "./features/navbar/MyNavbar";
import RouteComponent from "./RouteComponent";

export default function App() {
	return (
		<div id="mainContainer">
			<MyNavbar />
			<RouteComponent />
		</div>
	);
}
