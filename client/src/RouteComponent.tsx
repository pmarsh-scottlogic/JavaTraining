import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import Home from "./pages/Home";
import Login from "./pages/Login";
import NotFound from "./pages/NotFound";
import Signup from "./pages/Signup";

const RouteComponent = () => {
	return (
		<Router>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route path="/login" element={<Login />} />
				<Route path="/signup" element={<Signup />} />
				<Route path="*" element={<NotFound />} />
			</Routes>
		</Router>
	);
};

export default RouteComponent;
