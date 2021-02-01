import React, { useEffect, useRef, useState } from "react";

import { useLoginDialog } from '../login/LoginDialog'

import { postWiki } from './wikiActions';
import { useWikiReducer } from './wikiReducer';
import { useAppHeader } from '../components/AppHeader'
import FieldEdit from "../components/FieldEdit";

export default function WikiPage(props) {
	console.log(props.match.path)
	var file = props.match.path == "/ewiki/:file" ? props.match.params.file : 'index';

	const [ editMode, setEditMode] = useState(false);
	const [ tab, setTab ] = useState(0)
	const [ editTabMode, setEditTabMode] = useState(false);
	const textInput = useRef();
	const [ wikiState, dispatch ] = useWikiReducer();

	const loginDialog = useLoginDialog();
	useEffect(() => {
		getPage(file);
	}, [])
	useEffect(() => {
		if (wikiState.wiki != null && textInput.current != null) {
			textInput.current.value = wikiState.wiki[tab].contents;
			textInput.current.focus();
		}
	})

	const { setAffirm } = useAppHeader();

	const getPage = (file) => {
		postWiki(dispatch, 'get', {file}, setAffirm);
	};

	const saveFileContents = (id, file, page, contents) => {
		postWiki(dispatch, 'saveFile', {id, file, page, contents}, setAffirm);
	};

	const saveFileTab = (id, file, page) => {
		postWiki(dispatch, 'saveFileTab', {id, file, page}, setAffirm);
	};

	return <>
		{
			editTabMode
			? <div>
				Edit tab mode
				{wikiState.wiki.map((f, i) => <div>
					<FieldEdit value={f.page} updateFunc={(value) => {saveFileTab(f.id, f.file, value)}} blank='TAB_NAME'/>
				</div>)}
				<div id="buttonBlock">
					<button onClick={() => setEditTabMode(false)}>Cancel</button>
				</div>
			</div>
			: <div>
				{ !editMode &&
					<div id="content" dangerouslySetInnerHTML={{__html:
						 wikiState.wiki == null
							? 'Loading . . .'
							: wikiState.wiki.length == 1
								? wikiState.wiki[tab].contents
								: 'tabs to go' }}>
					</div>
				}
				{ !editMode && loginDialog.state.userInfo != null && <div id="buttonBlock">
					<button onClick={() => setEditTabMode(true)}>Edit tabs</button>
					<button onClick={() => setEditMode(true)}>Edit</button>
					</div>
				}
				{ editMode && <div className="content">
					<textarea id="fText" name="text" rows='30' cols='130' ref={textInput}></textarea><br/>
					<table>
						<tbody>
							<tr><td><button onClick={() => {setEditMode(false);saveFileContents(wikiState.wiki[tab].id, wikiState.wiki[tab].file, wikiState.wiki[tab].page, textInput.current.value)}}>Save</button></td>
								<td><button onClick={() => setEditMode(false)}>Cancel</button></td></tr>
						</tbody>
					</table>
					</div>
				}
			</div>
		}
	</>
}
