import React, {Component} from "react";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";

export class SliderType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(Number(event.target.value));
    };

    render() {
        return (
            <div className="slide_container">
                <output>{this.props.value}</output>
                <input type="range" min={this.props.min} max={this.props.max} step={this.props.step} className="slider"
                       value={this.props.value} onChange={this.onChangeValue} onBlur={this.props.onBlur}/>
            </div>
        );
    }
}

export class ButtonListType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(this.props.options[event.target.value].value);
    };

    render() {
        return (
            <React.Fragment>
                {this.props.options.map((value, index) => (
                    <React.Fragment key={"id-" + this.props.name + "-" + index}>
                        <input type="radio" value={index} checked={this.props.value === value.value}
                               name={"id-" + this.props.name} onChange={this.onChangeValue}
                               id={"id-" + this.props.name + "-" + index} onBlur={this.props.onBlur}/>
                        <label htmlFor={"id-" + this.props.name + "-" + index} data-color={value.color}>
                            {value.name}
                        </label>
                    </React.Fragment>
                ))}
            </React.Fragment>
        );
    }
}

export class StringInputType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(event.target.value);
    };

    render() {
        return (
            <input type="text" value={this.props.value} onChange={this.onChangeValue} onBlur={this.props.onBlur}/>
        );
    }
}

export class BooleanInputType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(event.target.checked);
    };

    render() {
        return (
            <label className="toggle">
                <input type="checkbox" checked={this.props.value} onChange={this.onChangeValue}
                       onBlur={this.props.onBlur}/>
                <span className="switch"/>
            </label>
        );
    }
}

export class NumberInputType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(event.target.value);
    };

    render() {
        return (
            <input type="number" min={this.props.min} max={this.props.max} className={this.props.bigger ? "bigger" : ""}
                   value={isNaN(this.props.value) ? "" : this.props.value} onChange={this.onChangeValue}
                   onBlur={this.props.onBlur}/>
        );
    }
}

export class ButtonInputType extends Component {
    onChangeValue = (event) => {
        this.props.onChangeValue(event.target.value);
    };

    render() {
        if (this.props.header) {
            return <button variant="link" title={this.props.description} onClick={this.onChangeValue}
                           onBlur={this.props.onBlur}>
                {this.props.value === "X" ? <FontAwesomeIcon icon={faTimes}/> : this.props.value}
            </button>;
        } else {
            return <button className="button blue small" title={this.props.description} onClick={this.onChangeValue}
                           onBlur={this.props.onBlur}>
                {this.props.value === "X" ? <FontAwesomeIcon icon={faTimes}/> : this.props.value}
            </button>;
        }
    }
}


export class SettingsInput extends Component {
    onChangeValue = (value) => {
        this.props.onChange(this.props.base.name, value);
    };

    render() {
        let className = this.props.base.borderless ? "borderless_box" : (!this.props.base.header ? "white_box" : "header_box");
        return (
            <div
                className={className + (this.props.base.type === "Boolean" ? " checkbox" : "")}>
                {(!this.props.base.borderless && <label className="legend" htmlFor="name">
                    {(((this.props.base.description && this.props.base.description.length > 0 && this.props.base.type !== "Button") || this.props.base.java !== this.props.base.bedrock)) &&
                        <span className="tooltip">
                            {this.props.base.java === true && this.props.base.bedrock === false &&
                                <strong>Java only.&nbsp;</strong>}
                            {this.props.base.java === false && this.props.base.bedrock === true &&
                                <strong>Bedrock only.&nbsp;</strong>}
                            {this.props.base.description}
                        </span>
                    }{this.props.name}
                </label>)}
                <div className="fields">
                    {this.props.base.type === "String" &&
                        <StringInputType value={this.props.base.value} onChangeValue={this.onChangeValue}
                                         onBlur={this.props.onBlur}/>
                    }
                    {(this.props.base.type === "Byte" || this.props.base.type === "Single" || this.props.base.type === "Int16" || this.props.base.type === "Int32" || this.props.base.type === "Int64" || this.props.base.type === "Double") &&
                        <NumberInputType value={this.props.base.value} min={this.props.base.min}
                                         max={this.props.base.max} bigger={this.props.base.bigger}
                                         onChangeValue={this.onChangeValue} onBlur={this.props.onBlur}/>
                    }
                    {this.props.base.type === "Boolean" &&
                        <BooleanInputType value={this.props.base.value} onChangeValue={this.onChangeValue}
                                          onBlur={this.props.onBlur}/>
                    }
                    {this.props.base.type === "Radio" &&
                        <ButtonListType name={this.props.name} options={this.props.base.options}
                                        value={this.props.base.value} onChangeValue={this.onChangeValue}
                                        onBlur={this.props.onBlur}/>
                    }
                    {this.props.base.type === "Slider" &&
                        <SliderType value={this.props.base.value} min={this.props.base.min} max={this.props.base.max}
                                    onChangeValue={this.onChangeValue} onBlur={this.props.onBlur}/>
                    }
                    {this.props.base.type === "Button" &&
                        <ButtonInputType value={this.props.base.value} description={this.props.base.description}
                                         onChangeValue={this.onChangeValue}
                                         onBlur={this.props.onBlur} header={this.props.base.header}/>
                    }
                </div>
            </div>
        );
    }
}