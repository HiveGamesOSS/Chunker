import React from "react";
import ReactDOM from "react-dom/client";
import "./css/main.css";
import "./css/mediaqueries.css";
import "./css/index.css";
import api from "./api";
import {PageRouter} from "./components/router";

const root = ReactDOM.createRoot(document.getElementsByTagName("body")[0]);
root.render(<PageRouter/>);

// Ensure api is closed on disconnect
window.addEventListener("beforeunload", () => api.close());