import {components} from "react-select"
import React from "react"

export const optimizeSelect = {
    components: {
        Option: NoMouseMoveOption,
    },
}

function NoMouseMoveOption(props) {
    delete props.innerProps.onMouseMove
    delete props.innerProps.onMouseOver
    return <components.Option {...props}>{props.children}</components.Option>
}