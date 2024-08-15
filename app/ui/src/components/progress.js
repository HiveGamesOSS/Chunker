import React, {Component} from "react";

export function Round2DP(number) {
    return number.toFixed(2) + "";
}

export class ProgressComponent extends Component {
    cancel = this.props.cancel === undefined ? true : this.props.cancel;

    constructor(props) {
        super(props);

        if (props.progress === undefined) {
            throw new Error("Undefined progress");
        }
    }

    render() {
        if (this.props.progress.isComplete()) {
            return (<div/>);
        } else {
            if (this.props.progress.isCancelled() || this.props.progress.isErrored()) {
                return (<div>
                    {this.props.progress.isErrored() ? (<h3>The task was unable to complete.</h3>) : (
                        <h3>This task was cancelled.</h3>)}
                </div>);
            } else {
                if (this.props.progress.state.queuePosition < 0) {
                    return (<div className={"progress_bar_container"}>
                        {!this.props.progress.isAnimated() &&
                            <h3>{this.props.progress.getName() || "Running"}: <span>{Round2DP(this.props.progress.getProgress())}%</span>
                            </h3>}
                        {this.props.progress.isAnimated() && <h3>{this.props.progress.getName() || "Running"}</h3>}
                        <div className={this.props.progress.isAnimated() ? "progress_bar animated" : "progress_bar"}>
                            {!this.props.progress.isAnimated() &&
                                <div className="progress_fill"
                                     style={{width: this.props.progress.getProgress() + "%"}}/>}
                        </div>
                        {this.props.progress.canCancel() && this.cancel &&
                            <div>
                                <h3>
                                    <button onClick={(e) => {
                                        e.preventDefault();
                                        this.props.progress.cancel();
                                    }}>Cancel
                                    </button>
                                </h3>
                                <br/></div>}
                    </div>);
                } else {
                    return (<div>
                        <h3>You are #{this.props.progress.getQueuePosition()} in the queue</h3>
                        {this.props.progress.canCancel() && this.cancel &&
                            <div>
                                <h3>
                                    <button onClick={(e) => {
                                        e.preventDefault();
                                        this.props.progress.cancel();
                                    }}>Cancel
                                    </button>
                                </h3>
                                <br/></div>}
                    </div>);
                }
            }
        }
    }
}

// Note: This uses functions over ES6 functions as react doesn't like them
export class ProgressTracker {
    cancelFunction = undefined;
    state = {
        name: "Running task...",
        progress: 0,
        queuePosition: 0,
        cancelled: false,
        errored: false,
        animated: false
    };

    constructor(name, updateState, cancelFunction) {
        this.cancelFunction = cancelFunction;
        this.update = updateState;

        this.state.name = name;
        this.state.canCancel = this.cancelFunction !== undefined;
    }

    isComplete = () => {
        return this.state.progress >= 100;
    };

    isCancelled = () => {
        return this.state.cancelled;
    };

    canCancel = () => {
        return this.state.canCancel;
    };

    isErrored = () => {
        return this.state.errored;
    };

    isAnimated = () => {
        return this.state.animated;
    };

    getName = () => {
        return this.state.name;
    };

    getProgress = () => {
        return this.state.progress;
    };

    getQueuePosition = () => {
        return this.state.queuePosition + 1;
    };

    updateState = (newValues) => {
        let oldState = Object.assign({}, this.state);
        let newState = Object.assign(oldState, newValues);
        this.state = newState;
        this.update(newState);
    };

    setProgress = (newProgress) => {
        this.updateState({progress: newProgress, queuePosition: -1});
    };

    setName = (newName) => {
        this.updateState({name: newName});
    };

    setAnimated = (newAnimated) => {
        this.updateState({animated: newAnimated});
    };

    setQueuePosition = (queuePosition) => {
        this.updateState({queuePosition: queuePosition});
    };

    cancel = () => {
        if (this.state.canCancel) {
            this.updateState({cancelled: true});
            this.cancelFunction();
        }
    };

    pipe = (handler) => {
        let self = this;
        return function (message) {
            if (message.continue === true) {
                if (message.type === "progress") {
                    self.setProgress(message.percentage * 100);
                }
                if (message.type === "progress_state") {
                    self.setProgress(message.percentage * 100);
                    self.setAnimated(message.animated);
                    if (message.name !== undefined) {
                        self.setName(message.name);
                    }
                }
                if (message.type === "queue_position") {
                    self.setQueuePosition(message.queuePosition);
                }
            } else {
                if (message.type === "error") {
                    if (message.cancelled === true) {
                        self.updateState({cancelled: true});
                    } else {
                        self.updateState({errored: true});
                    }
                } else {
                    if (self.state.progress < 100) {
                        self.setProgress(100);
                    }
                }
                handler(message);
            }
        };
    };
}