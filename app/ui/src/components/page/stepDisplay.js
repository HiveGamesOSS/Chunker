import React, {Component} from "react";

export class StepDisplay extends Component {
    render() {
        return (<div className="progress">
            <svg viewBox="0 0 420 89">
                <path className="bg"
                      d="M 37.5 0 L 75 21 L 75 29 L 115 29 L 115 21 L 152.5 0 L 190 21 L 190 29 L 230 29 L 230 21 L 267.5 0 L 305 21 L 305 29 L 345 29 L 345 21 L 382.5 0 L 420 21 L 420 67 L 382.5 89 L 345 67 L 345 59 L 305 59 L 305 67 L 267.5 89 L 230 67 L 230 59 L 190 59 L 190 67 L 152.5 89 L 115 67 L 115 59 L 75 59 L 75 67 L 37.5 89 L 0 67 L 0 21"></path>
                <g className={"step1 " + (this.props.stage >= 1 ? "complete" : "")}>
                    <path className="hex" d="M 37.5 7 L 70 26 L 70 63 L 37.5 82 L 5 63 L 5 26 L 37.5 7"></path>
                    <path className="shadow"
                          d="M 5 26 L 5 63 L 37.5 82 L 70 63 L 70 57 L 37.5 76 L 10 60 L 10 23"></path>
                    <path className="highlight"
                          d="M 5 26 L 37.5 7 L 70 26 L 70 63 L 65 66 L 65 29 L 37.5 13 L 5 32"></path>
                    <path className="corner1" d="M 5 26 L 10 23 L 10 29 L 5 32"></path>
                    <path className="corner2" d="M 65 60 L 70 57 L 70 63 L 65 66"></path>
                    <image href="images/hopper.png" x="21" y="28" height="30px" width="32px"/>
                </g>
                <g className={"step2 " + (this.props.stage >= 2 ? "complete" : "")}>
                    <path className="hex" d="M 37.5 7 L 70 26 L 70 63 L 37.5 82 L 5 63 L 5 26 L 37.5 7"></path>
                    <path className="shadow"
                          d="M 5 26 L 5 63 L 37.5 82 L 70 63 L 70 57 L 37.5 76 L 10 60 L 10 23"></path>
                    <path className="highlight"
                          d="M 5 26 L 37.5 7 L 70 26 L 70 63 L 65 66 L 65 29 L 37.5 13 L 5 32"></path>
                    <path className="corner1" d="M 5 26 L 10 23 L 10 29 L 5 32"></path>
                    <path className="corner2" d="M 65 60 L 70 57 L 70 63 L 65 66"></path>
                    <path className="line" d="M -36 40 Q -40 44 -36 48 L -4 48 Q 0 44 -4 40"></path>
                    <image href="images/cmdblock.png" x="21" y="26" height="36px" width="32px"/>
                </g>
                <g className={"step3 " + (this.props.stage >= 3 ? "complete" : "")}>
                    <path className="hex" d="M 37.5 7 L 70 26 L 70 63 L 37.5 82 L 5 63 L 5 26 L 37.5 7"></path>
                    <path className="shadow"
                          d="M 5 26 L 5 63 L 37.5 82 L 70 63 L 70 57 L 37.5 76 L 10 60 L 10 23"></path>
                    <path className="highlight"
                          d="M 5 26 L 37.5 7 L 70 26 L 70 63 L 65 66 L 65 29 L 37.5 13 L 5 32"></path>
                    <path className="corner1" d="M 5 26 L 10 23 L 10 29 L 5 32"></path>
                    <path className="corner2" d="M 65 60 L 70 57 L 70 63 L 65 66"></path>
                    <path className="line" d="M -36 40 Q -40 44 -36 48 L -4 48 Q 0 44 -4 40"></path>
                    <image href="images/furnace.png" x="21" y="25" height="36px" width="32px"/>
                </g>
                <g className={"step4 " + (this.props.stage >= 4 ? "complete" : "")}>
                    <path className="hex" d="M 37.5 7 L 70 26 L 70 63 L 37.5 82 L 5 63 L 5 26 L 37.5 7"></path>
                    <path className="shadow"
                          d="M 5 26 L 5 63 L 37.5 82 L 70 63 L 70 57 L 37.5 76 L 10 60 L 10 23"></path>
                    <path className="highlight"
                          d="M 5 26 L 37.5 7 L 70 26 L 70 63 L 65 66 L 65 29 L 37.5 13 L 5 32"></path>
                    <path className="corner1" d="M 5 26 L 10 23 L 10 29 L 5 32"></path>
                    <path className="corner2" d="M 65 60 L 70 57 L 70 63 L 65 66"></path>
                    <path className="line" d="M -36 40 Q -40 44 -36 48 L -4 48 Q 0 44 -4 40"></path>
                    <image href="images/dropper.png" x="22" y="26" height="36px" width="32px"/>
                </g>
            </svg>
        </div>);
    }
}