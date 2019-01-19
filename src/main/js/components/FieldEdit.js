import React from 'react';

// properties
// multiLine = boolean
// value = value to display
// updateFunc = function to call when update
// blank = value to display if blank
export default class FieldEdit extends React.Component{

    constructor(props) {
        super(props);
        this.state = { editMode: false };
        this.textInput = React.createRef();
        this.keyDown = this.keyDown.bind(this);
        this.keyDownEsc = this.keyDownEsc.bind(this);
    }

    componentDidUpdate() {
        if (this.state.editMode) {
            this.textInput.current.value = this.props.value;
            this.textInput.current.focus();
        }
    }

    setEditMode(mode) {
        if (mode != this.state.editMode) {
            this.setState({ editMode: mode});
        }
    }

    saveValue() {
        this.props.updateFunc(this.textInput.current.value)
        this.setState({ editMode: false});
    }

    keyDown(e) {
        if (!this.props.multiLine && e.keyCode == 13) { // enter
            this.saveValue();
        }
        this.keyDownEsc(e);
    }

    keyDownEsc(e) {
        if (e.keyCode == 27) { // escape
            this.setEditMode(false);
        }
    }

	render() {
        const { value, blank, multiLine } = this.props;
        const { editMode } = this.state;
        
        const viewValue = value == '' ? <span style={{color: 'gray'}}><i>{blank}</i></span> : value;

        const editValue = multiLine
            ? <span><textarea onKeyDown={this.keyDownEsc} ref={this.textInput} style={{width: '900px', height: '300px'}}></textarea>
                <span onClick={() => this.saveValue()} style={{color: 'green', cursor: 'pointer'}}>&#x2714;</span>
                <span onClick={() => this.setEditMode(false)} style={{color: 'red', cursor: 'pointer'}}>&#x2718;</span>
              </span>
            : <input onKeyDown={this.keyDown} onBlur={() => this.setEditMode(false)} ref={this.textInput} type='text' />;

        return <span onClick={() => this.setEditMode(true)}>
                { editMode ? editValue : viewValue}
			</span>
	}
}
