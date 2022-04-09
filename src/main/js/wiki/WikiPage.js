import React, { useEffect, useRef, useState } from "react";

import { useLoginDialog } from '../login/LoginDialog'

import { postWiki } from './wikiActions';
import { useWikiReducer } from './wikiReducer';
import { useAppHeader } from '../components/AppHeader'
import { useParams } from "react-router-dom";

export default function WikiPage(props) {

	const params = useParams();
	var pageName = params.pageName != null ? params.pageName : 'index';

	const [ editMode, setEditMode] = useState(false)
	const textInput = useRef();
	const [ wikiState, dispatch ] = useWikiReducer();

	const loginDialog = useLoginDialog();
	useEffect(() => {
		getPage(pageName);
	}, [])
	useEffect(() => {
		if (wikiState.wiki != null && wikiState.wiki.fileText != null && textInput.current != null) {
			textInput.current.value = wikiState.wiki.fileText;
			textInput.current.focus();
		}
	})

	const { setAffirm } = useAppHeader();

	const getPage = (fileName) => {
		postWiki(dispatch, 'get', {fileName}, setAffirm);
	};

	const savePage = (fileName, fileText) => {
		postWiki(dispatch, 'save', {fileName, fileText}, setAffirm);
	};

	return <div>
		{ !editMode && <div id="content" dangerouslySetInnerHTML={{__html: wikiState.wiki == null ? '' : wikiState.wiki.fileText == null ? 'File ' + wikiState.wiki.fileName + ' does not exist' : wikiState.wiki.fileText }}>
			</div>
		}
		{ !editMode && loginDialog.state.userInfo != null && <div id="buttonBlock">
			<button onClick={() => setEditMode(true)}>Edit</button>
			</div>
		}
		{ editMode && <div className="content">
			<textarea id="fText" name="text" rows='30' cols='130' ref={textInput}></textarea><br/>
			<table>
				<tbody>
					<tr><td><button onClick={() => {setEditMode(false);savePage(wikiState.wiki.fileName, textInput.current.value)}}>Save</button></td>
						<td><button onClick={() => setEditMode(false)}>Cancel</button></td></tr>
				</tbody>
			</table>
			</div>
		}
	</div>
}
