import {Component} from "react";

export class BaseScreen extends Component {
    app = this.props.app;

    componentDidMount() {
        this.app.setStage(this.getStage());
    }

    getStage = () => {
        return 1;
    };
}