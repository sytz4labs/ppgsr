import React from 'react';

export default class FieldEdit extends React.Component{

    constructor(props) {
        super(props);
        this.state = { editMode: false, editValue: '' };
        this.toggle = this.toggle.bind(this);
        this.keyDown = this.keyDown.bind(this);
        this.myChange = this.myChange.bind(this);
    }

    componentDidUpdate() {
        if (this.state.editMode) {
            this.valueInput.focus();
        }
    }

    toggle(e) {
        this.setState(prevState => ({
            editMode: !prevState.editMode,
            editValue: this.props.value
        }))
    }

    keyDown(e) {
        if (e.keyCode == 13) {
            this.props.setFunc(e.target.value);
            this.toggle(e);
        }
    }

    myChange(e) {
        const a = e.target.value;
        this.setState(prevState => ({
            editMode: prevState.editMode,
            editValue: a
        }))
    }

	render() {
	    const { value } = this.props;

		return <span>
            {this.state.editMode
                ? <table><tbody><tr><td><input ref={(input) => {this.valueInput = input;}}
                                            className="input__field input--small input-skin"
                                            name="iname"
                                            onChange={this.myChange} 
                                            value={this.state.editValue} 
                                            onKeyDown={this.keyDown} /></td>
                        <td><p name="cancel" style={{color: "red", cursor: 'pointer'}} onClick={this.toggle}><b>X</b></p></td></tr></tbody></table>
                : <span onClick={this.toggle} style={{cursor: 'pointer', borderBottom: '1px dashed #00d'}}>{value}</span>
            }
			</span>
	}
}
