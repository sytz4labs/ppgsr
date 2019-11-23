import React from 'react';
import { connect } from "react-redux";

import { postWiki } from './wikiActions';

class WikiPage extends React.Component{

	constructor(props) {
        super(props);
        this.state = { editMode: false };
		this.textInput = React.createRef();
	}

    componentDidMount() {
		var pageName = this.props.match.path == "/wiki/:pageName" ? this.props.match.params.pageName : 'index';
		this.props.getPage(pageName);
    }

	componentDidUpdate() {
        if (this.state.editMode) {
			var value = this.props.wikiR.wiki == null ? '' : this.props.wikiR.wiki.fileText;
			this.textInput.current.value = value;
			this.textInput.current.focus();
        }
    }

	setEditMode(mode) {
        if (mode != this.state.editMode) {
            this.setState({ editMode: mode});
        }
	}
	
	render() {
		const { wiki } = this.props.wikiR;
		const { userInfo } = this.props.loginR;

		return <div>
			{ !this.state.editMode && <div className="content" dangerouslySetInnerHTML={{__html: wiki == null ? '' : wiki.fileText}}>
				</div>
			}
			{ !this.state.editMode && userInfo != null && <div id="buttonBlock">
		 		<button onClick={() => this.setEditMode(true)}>Edit</button>
				</div>
			}
			{ this.state.editMode && <div className="content">
				<textarea id="fText" name="text" rows='30' cols='130' ref={this.textInput}></textarea><br/>
				<table>
					<tbody>
						<tr><td><button onClick={() => {this.setEditMode(false);this.props.savePage(wiki.fileName, this.textInput.current.value)}}>Save</button></td>
							<td><button onClick={() => this.setEditMode(false)}>Cancel</button></td></tr>
					</tbody>
				</table>
				</div>
			}
		</div>
	}
};

const mapStateToProps = state => {
	return {
		wikiR: state.wikiReducer,
		loginR: state.loginReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		getPage: (fileName) => {
			dispatch(postWiki('get', {fileName}))
		},
		savePage: (fileName, fileText) => {
			dispatch(postWiki('save', {fileName, fileText}))
		},
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(WikiPage);