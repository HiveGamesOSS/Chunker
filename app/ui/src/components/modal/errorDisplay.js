import React, {Component} from "react";

export class ErrorDisplay extends Component {
    modal = React.createRef();
    state = {
        title: "Could not load App",
        body: "Something went wrong trying to load the app :(",
        canClose: true,
        stackTrace: undefined,
        errorId: undefined
    };

    render() {
        let url = this.props.app.generateIssueLink(this.state.body, this.state.stackTrace);
        return (
            <div className="modal_overlay">
                <div className="modal">
                    <h3>Oops, {this.state.title}</h3>
                    <p>{this.state.body}<br/>If this error continues please report it to us via our <a target="_blank"
                                                                                                       rel="noreferrer"
                                                                                                       href={url}>GitHub
                        issues</a>.</p>
                    <p>
                        {this.state.canClose &&
                            <button className="button green" onClick={this.props.close}>Close</button>
                        }
                        {!this.state.canClose &&
                            <button className="button blue" onClick={() => document.location.reload()}>Restart</button>
                        }
                    </p>
                    {this.state.errorId &&
                        <p>Error identifier: <span className="code">{this.state.errorId}</span></p>
                    }
                </div>
            </div>
        );
    }
}
