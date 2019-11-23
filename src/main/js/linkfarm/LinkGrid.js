import React from 'react';
import { connect } from "react-redux"
import Modal from 'react-modal';

import loginReducer from '../login/loginReducer'
import { getLinks, postLinks } from "./linkActions"
import FieldEdit from "../components/FieldEdit"

Modal.setAppElement('#react');

class LinkGrid extends React.Component{

    constructor(props) {
        super(props);
		this.state = { linkEditMode: false };
		this.handleInputChange = this.handleInputChange.bind(this);
	}

    componentDidMount() {
		this.pageName = this.props.match.path == "/lf/:pageName" ? this.props.match.params.pageName : '';
		this.props.getLinks(this.pageName);
    }

	handleInputChange(event) {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
		const name = target.name;
	
		this.setState({
		  [name]: value
		});
	}

	render() {
		if (this.props.linksR.links == null) {
			return <div>Loading...</div>
		}

		const { error, info, success} = this.props.linksR.links;
		const { userInfo } = this.props.loginR;

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
										: <FieldEdit value={grp.name} updateFunc={(value) => {this.props.renameGroupCmd(this.pageName, colOrd, grpOrd, value)}} blank='GROUP_NAME'/>
									}
								</span>
								{ userInfo != null && <span className='edit'>
									<span onClick={() => {this.props.groupCmd('moveGroupLeft', this.pageName, colOrd, grpOrd)}}> &#x25C4;</span>
									<span onClick={() => {this.props.groupCmd('moveGroupUp', this.pageName, colOrd, grpOrd)}}> &#x25B2;</span>
									<span onClick={() => {this.props.groupCmd('moveGroupDown', this.pageName, colOrd, grpOrd)}}> &#x25BC;</span>
									<span onClick={() => {this.props.groupCmd('moveGroupRight', this.pageName, colOrd, grpOrd)}}> &#x25BA;</span>
									<span onClick={() => {this.props.groupCmd('newGroupLink', this.pageName, colOrd, grpOrd)}}> &#x21b2;</span>
									<span onClick={() => {this.props.removeGroupCmd(this.pageName, colOrd, grpOrd)}}> &#x1f5d1;</span>
								</span> }
								<br/>
								<p>
								{grp.links.map((link, linkOrd) =>
									<span key={linkOrd}>
										<a href={link.href}>{link.name}</a>
										{userInfo != null && <span className='edit'>
											<span onClick={() => {this.setState({linkEditMode: true, page: this.pageName, colOrd: colOrd, grpOrd: grpOrd, linkOrd: linkOrd, name: link.name, url: link.href, newColGrp: colOrd + ',' + grpOrd})}}> &#x270D;</span>
											<span onClick={() => {this.props.linkCmd('moveLinkUp', this.pageName, colOrd, grpOrd, linkOrd)}}> &#x25B2;</span>
											<span onClick={() => {this.props.linkCmd('moveLinkDown', this.pageName, colOrd, grpOrd, linkOrd)}}> &#x25BC;</span>
										</span>}
										<br/>
									</span>)}
								</p>
								<br/>
							</span>)
				}
				{userInfo != null && <FieldEdit value='' updateFunc={(value) => {this.props.newGroupCmd(this.pageName, colOrd, value)}} blank='NEW_GROUP'/>}
			</div>)}
				<Modal  isOpen={this.state.linkEditMode} onRequestClose={() => this.setState({ linkEditMode: false })} >
					Hello{this.state.linkEditMode ? <span>{'page ' + this.state.page + ' ' + this.state.colOrd + ' ' + this.state.grpOrd + ' ' + this.state.linkOrd}</span> : ''}
					<form>
						<input type='hidden' name='page' value={this.state.page} />
						<input type='hidden' name='colOrd' value={this.state.colOrd} />
						<input type='hidden' name='grpOrd' value={this.state.grpOrd} />
						<input type='hidden' name='linkOrd' value={this.state.linkOrd} />
						<table>
							<tbody>
								<tr>
									<th>Group</th>
									<td><select name='newColGrp' value={this.state.newColGrp} onChange={this.handleInputChange}>{
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
									<td><input type='text' name='name' value={this.state.name} onChange={this.handleInputChange} /></td>
								</tr>
								<tr>
									<th>Link URL</th>
									<td><input type='text' name='url' value={this.state.url} onChange={this.handleInputChange} /></td>
								</tr>
								<tr>
									<th></th>
									<td>
										<button onClick={(e) => {
											this.props.saveLinkCmd(this.state);
											e.preventDefault();
											this.setState({...this.state, linkEditMode: false})
											}}>Save</button>
										&nbsp;
										<button onClick={(e) => {
											if (confirm("Delete, REALLY?")) {
												this.props.removeLinkCmd(this.state);
												e.preventDefault();
												this.setState({...this.state, linkEditMode: false})
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
};

const mapStateToProps = state => {
	return {
		linksR: state.linksReducer,
		loginR: state.loginReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		getLinks: (page) => {
			dispatch(getLinks(page))
		},
		linkCmd: (cmd, pageName, colOrd, grpOrd, linkOrd) => {
			dispatch(postLinks({ cmd: cmd, page: pageName, colOrd: colOrd, grpOrd: grpOrd, linkOrd: linkOrd}))
		},
		saveLinkCmd: (li) => {
			dispatch(postLinks({cmd: 'saveLink', page: li.page, colOrd: li.colOrd, grpOrd: li.grpOrd, linkOrd: li.linkOrd, name: li.name, url: li.url, newColGrp: li.newColGrp}))
		},
		removeLinkCmd: (li) => {
			dispatch(postLinks({cmd: 'removeLink', page: li.page, colOrd: li.colOrd, grpOrd: li.grpOrd, linkOrd: li.linkOrd}))
		},
		newGroupCmd: (pageName, colOrd, newValue) => {
			dispatch(postLinks({ cmd: 'newGroup', page: pageName, colOrd: colOrd, name: newValue }))
		},
		groupCmd: (cmd, pageName, colOrd, grpOrd) => {
			dispatch(postLinks({ cmd: cmd, page: pageName, colOrd: colOrd, grpOrd: grpOrd }))
		},
		renameGroupCmd: (pageName, colOrd, grpOrd, newValue) => {
			dispatch(postLinks({ cmd: 'renameGroup', page: pageName, colOrd: colOrd, grpOrd: grpOrd, name: newValue }))
		},
		removeGroupCmd: (pageName, colOrd, grpOrd) => {
			if (confirm('Are you sure you want to delete')) {
				dispatch(postLinks({ cmd: 'removeGroup', page: pageName, colOrd: colOrd, grpOrd: grpOrd }))
			}
		},
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(LinkGrid);