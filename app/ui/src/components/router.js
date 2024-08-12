import React, {Component} from "react";
import {HashRouter as Router, Route, Routes} from "react-router-dom";
import App from "./app";

export class PageRouter extends Component {
    render() {
        return (
            <Router>
                <Routes>
                    <Route index element={<App/>}/>
                </Routes>
            </Router>
        );
    }
}

export default App;