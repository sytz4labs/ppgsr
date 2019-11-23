import React from 'react';
import { connect } from "react-redux"
import { getConfigs, saveConfig } from "./configActions"
import FieldEdit from "../components/FieldEdit"

class ConfigGrid extends React.Component{

    componentDidMount() {
		this.props.getConfigs();
    }

	render() {
		return <div id='configTable'>
            <table>
                <tbody>
                    {this.props.configsR.configs.map(c => <tr key={c.id}>
                        <td><FieldEdit value={c.name} updateFunc={(value) => {this.props.saveConfig(c.id, 'name', value)}} multiLine={false} blank='BLANK'/></td>
                        <td><FieldEdit value={c.value} updateFunc={(value) => {this.props.saveConfig(c.id, 'value', value)}} multiLine={c.multiLine} blank='BLANK'/></td>
                    </tr>)}
                    <tr>
                        <td><FieldEdit value='' updateFunc={(value) => {this.props.saveConfig(-1, 'name', value)}} blank='NEW'/></td>
                    </tr>
                </tbody>
            </table>
        </div>
	}
};

const mapStateToProps = state => {
	return {
		configsR: state.configsReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		getConfigs: () => {
			dispatch(getConfigs())
		},
		saveConfig: (id, type, value) => {
			dispatch(saveConfig(id, type, value))
		}
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(ConfigGrid);