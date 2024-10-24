import React, { useEffect, useRef, useState } from "react";

import { useLoginDialog } from '../login/LoginDialog'

import { postWiki } from './wikiActions';
import { useWikiReducer } from './wikiReducer';
import { useAppHeader } from '../components/AppHeader'
import FieldEdit from "../components/FieldEdit";
import { Tab, TabSet } from "../components/TabSet";
import { useParams } from "react-router-dom";

export default function WikiPage() {

	const params = useParams();
	var page = params.page != null ? params.page : 'index';

	const [ editTabMode, setEditTabMode] = useState(false);
	const [ wikiState, dispatch ] = useWikiReducer();

	const loginDialog = useLoginDialog();
	useEffect(() => {
		getPage(page);
	}, [])

	const { setAffirm } = useAppHeader();

	const getPage = (page) => {
		postWiki(dispatch, 'get', {page}, setAffirm);
	};

	const savePageContents = (id, page, tab, contents) => {
		postWiki(dispatch, 'savePage', {id, page, tab, contents}, setAffirm);
	};

	const savePageTab = (id, sort, page, tab) => {
		postWiki(dispatch, 'savePageTab', {id, sort, page, tab}, setAffirm);
	};

	const saveMoveTab = (direction, id, page) => {
		postWiki(dispatch, 'saveMoveTab/' + direction, {id, page});
	};

	return <>
		{
			editTabMode
				? <div>
					Edit tab mode
					<div id="buttonBlock">
						<button onClick={() => setEditTabMode(false)}>Done</button>
					</div>
					{wikiState.wiki.map((f, i) => <div key={i}>
						<span className='edit'>
							<span onClick={() => {saveMoveTab('up', f.id, f.page)}}> &#x25B2;</span>
							<span onClick={() => {saveMoveTab('down', f.id, f.page)}}> &#x25BC;</span>
						</span>
						<FieldEdit value={f.tab} updateFunc={(value) => {savePageTab(f.id, f.sort, f.page, value)}} blank='TAB_NAME'/>
					</div>)}
					<FieldEdit value='' updateFunc={(value) => {savePageTab(-1, -1, page, value)}} blank='NEW_TAB'/>
				</div>
				: <div>
					{ wikiState.wiki == null
						? 'Loading . . .'
						: wikiState.wiki.length == 1
							? <div className='s4lTabsContent'>
								<WikiTab tab={wikiState.wiki[0]}
									editAllowed={loginDialog.state.userInfo != null}
									setEditTabModeFunc={setEditTabMode}
									savePageContentsFunc={savePageContents} />
								</div>
							: <TabSet>
								{wikiState.wiki.map((t, i) => <Tab key={i} name={t.tab}>
									<WikiTab tab={t}
										editAllowed={loginDialog.state.userInfo != null}
										setEditTabModeFunc={setEditTabMode}
										savePageContentsFunc={savePageContents} />
									</Tab>)}
							</TabSet>
					}
				</div>
		}
	</>
}

/* props
	tab
	editAllowed
	setEditTabModeFunc
	savePageContentsFunc
*/
function WikiTab(props) {

	const { id, page, tab, contents } = props.tab

	const textInput = useRef();

	const [ editMode, setEditMode] = useState(false);
	useEffect(() => {
		if (textInput.current != null) {
			textInput.current.value = contents;
			textInput.current.focus();
		}
	})

	return <>
			{!editMode && props.editAllowed && <div id="buttonBlock">
				<button onClick={() => props.setEditTabModeFunc(true)}>Edit tabs</button>
				<button onClick={() => setEditMode(true)}>Edit</button>
				</div>
			}
			{!editMode && <div dangerouslySetInnerHTML={{__html: contents}}></div>}
			{ editMode && <div className="content">
				<textarea id="fText" name="text" rows='40' cols='200' ref={textInput}></textarea><br/>
				<table>
					<tbody>
						<tr><td><button onClick={() => {setEditMode(false);props.savePageContentsFunc(id, page, tab, textInput.current.value)}}>Save</button></td>
							<td><button onClick={() => setEditMode(false)}>Close</button></td></tr>
					</tbody>
				</table>
				</div>
			}
		</>
}