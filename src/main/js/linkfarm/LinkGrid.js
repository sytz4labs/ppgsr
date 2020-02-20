import React, { useEffect, useState } from 'react';
import Modal from 'react-modal';

import { getLinks, postLinks } from "./linkActions"
import FieldEdit from "../components/FieldEdit"
import { useLinksReducer } from './linksReducer';
import { useLoginDialog } from '../login/LoginDialog';

Modal.setAppElement('#react');

export default function LinkGrid(props) {

	const pageName = props.match.path == "/lf/:pageName" ? props.match.params.pageName : '';

	const [ linkState, setLinkState ] = useState({ linkEditMode: false })
	const [ linksR, dispatch ] = useLinksReducer();
	const loginContext = useLoginDialog();
	useEffect(() => {
		getLinks(dispatch, pageName)
	}, []);

	if (linksR.links == null) {
		return <div>Loading...</div>
	}

	const handleInputChange = (event) => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
		const name = target.name;
	
		setLinkState({...linkState, [name]: value});
	}

	const { error, info, success} = linksR.links;
	const userInfo = loginContext.state.userInfo;

	const linkCmd = (cmd, pageName, colOrd, grpOrd, linkOrd) => {
		postLinks(dispatch, { cmd: cmd, page: pageName, colOrd: colOrd, grpOrd: grpOrd, linkOrd: linkOrd})
	};
	const saveLinkCmd = (li) => {
		postLinks(dispatch, {cmd: 'saveLink', page: li.page, colOrd: li.colOrd, grpOrd: li.grpOrd, linkOrd: li.linkOrd, name: li.name, url: li.url, newColGrp: li.newColGrp})
	};
	const removeLinkCmd = (li) => {
		postLinks(dispatch, {cmd: 'removeLink', page: li.page, colOrd: li.colOrd, grpOrd: li.grpOrd, linkOrd: li.linkOrd})
	};
	const newGroupCmd = (pageName, colOrd, newValue) => {
		postLinks(dispatch, { cmd: 'newGroup', page: pageName, colOrd: colOrd, name: newValue });
	};
	const groupCmd = (cmd, pageName, colOrd, grpOrd) => {
		postLinks(dispatch, { cmd: cmd, page: pageName, colOrd: colOrd, grpOrd: grpOrd });
	};
	const renameGroupCmd = (pageName, colOrd, grpOrd, newValue) => {
		postLinks(dispatch, { cmd: 'renameGroup', page: pageName, colOrd: colOrd, grpOrd: grpOrd, name: newValue });
	};
	const removeGroupCmd = (pageName, colOrd, grpOrd) => {
		if (confirm('Are you sure you want to delete')) {
			postLinks(dispatch, { cmd: 'removeGroup', page: pageName, colOrd: colOrd, grpOrd: grpOrd });
		}
	};

	return <div>
		<div style={{color: 'green'}}>{success}</div>
		<div style={{color: 'red'}}>{error}</div>
		<div id='content'>
			{info.cols.map((col, colOrd) =>
				<div className="column" key={colOrd}>{
					col.groups.map((grp, grpOrd) =>
						<span key={grpOrd}>
							<span className="groupName">
								{userInfo == null
									? grp.name
									: <FieldEdit value={grp.name} updateFunc={(value) => {renameGroupCmd(pageName, colOrd, grpOrd, value)}} blank='GROUP_NAME'/>
								}
							</span>
							{ userInfo != null && <span className='edit'>
								<span onClick={() => {groupCmd('moveGroupLeft', pageName, colOrd, grpOrd)}}> &#x25C4;</span>
								<span onClick={() => {groupCmd('moveGroupUp', pageName, colOrd, grpOrd)}}> &#x25B2;</span>
								<span onClick={() => {groupCmd('moveGroupDown', pageName, colOrd, grpOrd)}}> &#x25BC;</span>
								<span onClick={() => {groupCmd('moveGroupRight', pageName, colOrd, grpOrd)}}> &#x25BA;</span>
								<span onClick={() => {groupCmd('newGroupLink', pageName, colOrd, grpOrd)}}> &#x21b2;</span>
								<span onClick={() => {removeGroupCmd(pageName, colOrd, grpOrd)}}> &#x1f5d1;</span>
							</span> }
							<br/>
							<p>
							{grp.links.map((link, linkOrd) =>
								<span key={linkOrd}>
									<a href={link.href}>{link.name}</a>
									{userInfo != null && <span className='edit'>
										<span onClick={() => {setLinkState({linkEditMode: true, page: pageName, colOrd: colOrd, grpOrd: grpOrd, linkOrd: linkOrd, name: link.name, url: link.href, newColGrp: colOrd + ',' + grpOrd})}}> &#x270D;</span>
										<span onClick={() => {linkCmd('moveLinkUp', pageName, colOrd, grpOrd, linkOrd)}}> &#x25B2;</span>
										<span onClick={() => {linkCmd('moveLinkDown', pageName, colOrd, grpOrd, linkOrd)}}> &#x25BC;</span>
									</span>}
									<br/>
								</span>)}
							</p>
							<br/>
						</span>)
			}
			{userInfo != null && <FieldEdit value='' updateFunc={(value) => {newGroupCmd(pageName, colOrd, value)}} blank='NEW_GROUP'/>}
		</div>)}
			<Modal  isOpen={linkState.linkEditMode} onRequestClose={() => setLinkState({ linkEditMode: false })} >
				<form>
					<input type='hidden' name='page' value={linkState.page} />
					<input type='hidden' name='colOrd' value={linkState.colOrd} />
					<input type='hidden' name='grpOrd' value={linkState.grpOrd} />
					<input type='hidden' name='linkOrd' value={linkState.linkOrd} />
					<table>
						<tbody>
							<tr>
								<th>Group</th>
								<td><select name='newColGrp' value={linkState.newColGrp} onChange={handleInputChange}>{
										info.cols.map((col, colOrd) => 
											col.groups.map((grp, grpOrd) => 
												<option key={colOrd+','+grpOrd} value={colOrd+','+grpOrd}>{grp.name}</option>
											)
										)
									}
									</select></td>
							</tr>
							<tr>
								<th>Link Name</th>
								<td><input type='text' name='name' value={linkState.name} onChange={handleInputChange} /></td>
							</tr>
							<tr>
								<th>Link URL</th>
								<td><input type='text' name='url' value={linkState.url} onChange={handleInputChange} /></td>
							</tr>
							<tr>
								<th></th>
								<td>
									<button onClick={(e) => {
										saveLinkCmd(linkState);
										e.preventDefault();
										setLinkState({...linkState, linkEditMode: false})
										}}>Save</button>
									&nbsp;
									<button onClick={(e) => {
										if (confirm("Delete, REALLY?")) {
											removeLinkCmd(linkState);
											e.preventDefault();
											setLinkState({...linkState, linkEditMode: false})
											}
										}}>Delete</button>
								</td>
							</tr>
						</tbody>
					</table>
				</form>
			</Modal>
		</div>
	</div>
}
