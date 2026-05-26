import React, {Component} from "react";
import {PreviewComponent} from "./preview/previewComponent";

export class PreviewTab extends Component {
    app = this.props.app;

    render() {
        // PreviewComponent owns the full loading sequence (settings → generation → tile
        // pre-load → Map) so the user sees a single, consistently-styled progress screen
        // through all phases instead of a brief "Grabbing world information" pane in a
        // different wrapper before our preview-side bars take over.
        return (
            <PreviewComponent app={this.app} session={this.app.state.sessionData.session}/>
        );
    }
}