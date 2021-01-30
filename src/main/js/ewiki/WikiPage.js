import React, { useEffect, useRef, useState } from "react";

import { useLoginDialog } from '../login/LoginDialog'

import { postWiki } from './wikiActions';
import { useWikiReducer } from './wikiReducer';
import { useAppHeader } from '../components/AppHeader'

export default function WikiPage(props) {
	console.log(props.match.path)
	var file = props.match.path == "/ewiki/:file" ? props.match.params.file : 'index';

	const [ editMode, setEditMode] = useState(false)
	const textInput = useRef();
	const [ wikiState, dispatch ] = useWikiReducer();

	const loginDialog = useLoginDialog();
	useEffect(() => {
		getPage(file);
	}, [])
	useEffect(() => {
		if (wikiState.wiki != null && wikiState.wiki.contents != null && textInput.current != null) {
			textInput.current.value = wikiState.wiki.contents;
			textInput.current.focus();
		}
	})

	const { setAffirm } = useAppHeader();

	const getPage = (file) => {
		postWiki(dispatch, 'get', {file}, setAffirm);
	};

	const savePage = (file, page, contents) => {
		postWiki(dispatch, 'save', {file, page, contents}, setAffirm);
	};

	return <div>
		{ !editMode && <div id="content" dangerouslySetInnerHTML={{__html: wikiState.wiki == null ? '' : wikiState.wiki.contents == null ? 'File ' + wikiState.wiki.file + ' does not exist' : wikiState.wiki.contents }}>
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
					<tr><td><button onClick={() => {setEditMode(false);savePage(wikiState.wiki.file, wikiState.wiki.page, textInput.current.value)}}>Save</button></td>
						<td><button onClick={() => setEditMode(false)}>Cancel</button></td></tr>
				</tbody>
			</table>
			</div>
		}
	</div>
}
