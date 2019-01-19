import React from 'react';

// properties
// value = value to display
// updateFunc(value) = function to call when update
// options = value to display if blank array of value: and text:
export default class DropEdit extends React.Component{

    constructor(props) {
        super(props);
        this.state = { editMode: false };
    }

    setEditMode(mode) {
        if (mode != this.state.editMode) {
            this.setState({ editMode: mode});
        }
    }

    handleInputChange(event) {
		const target = event.target;

        this.props.updateFunc(target.value);
		this.setState({ editMode: false });
	}

	render() {
        const { value, options } = this.props;
        const { editMode } = this.state;
        
        const displayf = options.filter((opt, optOrd) => opt.value == value);
        console.log(displayf[0])
        const display = displayf[0].text;

        return <span>
                { editMode
                    ? <select value={this.props.value} style={{cursor: 'pointer'}} onChange={(e) => this.handleInputChange(e)} onBlur={() => this.setEditMode(false)}>
                        {options.map((opt, optOrd) => 
                            <option key={optOrd} value={opt.value}>{opt.text}</option>)}
                    </select>
                    : <span style={{cursor: 'pointer'}} onClick={() => this.setEditMode(true)}>{display}</span>}
            </span>
			
	}
}
