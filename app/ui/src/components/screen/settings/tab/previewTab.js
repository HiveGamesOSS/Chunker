import React, {Component} from "react";
import {PreviewComponent} from "./preview/previewComponent";
import {ProgressComponent} from "../../../progress";

export class PreviewTab extends Component {
    app = this.props.app;

    render() {
        return (
            <React.Fragment>
                {(!this.app.settingsProgress.isComplete() &&
                    <div className="main_content">
                        <ProgressComponent progress={this.app.settingsProgress}/>
                    </div>
                )}
                {(this.app.settingsProgress.isComplete() &&
                    <PreviewComponent app={this.app} session={this.app.state.sessionData.session}/>
                )}
            </React.Fragment>
        );
    }
}