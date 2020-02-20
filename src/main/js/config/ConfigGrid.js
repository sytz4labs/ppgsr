import React, { useEffect } from 'react';
import { useConfigsReducer } from './configsReducer'
import { getConfigs, saveConfig } from './configActions'
import FieldEdit from '../components/FieldEdit'

export default function ConfigGrid() {

	const [ configState, dispatch ] = useConfigsReducer();
	useEffect(() => {
		getConfigs(dispatch);
	}, []);

	return <div id='configTable'>
		<table>
			<tbody>
				{configState.configs.map(c => <tr key={c.id}>
					<td><FieldEdit value={c.name} updateFunc={(value) => {saveConfig(dispatch, c.id, 'name', value)}} multiLine={false} blank='BLANK'/></td>
					<td><FieldEdit value={c.value} updateFunc={(value) => {saveConfig(dispatch, c.id, 'value', value)}} multiLine={c.multiLine} blank='BLANK'/></td>
				</tr>)}
				<tr>
					<td><FieldEdit value='' updateFunc={(value) => {saveConfig(dispatch, -1, 'name', value)}} blank='NEW'/></td>
				</tr>
			</tbody>
		</table>
	</div>
}