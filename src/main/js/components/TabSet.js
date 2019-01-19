import React from 'react';

export class TabSet extends React.Component{

    constructor(props) {
        super(props);
		this.state = { tabSelected: 0 };
	}

	tabChildFilter(child) {
		return React.isValidElement(child);
	}

	selectTab(ord) {
		this.setState({ tabSelected: ord})
	}

	render() {
		return <div className='s4lTabs'>
				{this.props.children.filter(this.tabChildFilter).map((child, childOrd) =>
					<div key={childOrd} className={'s4lTab' + (childOrd == this.state.tabSelected ? ' s4lSelected' : '')}
						onClick={(e) => this.selectTab(childOrd)}>
						{child.props.name}
					</div>)}
				<div className='s4lTabsContent'>
					{this.props.children.filter(this.tabChildFilter).filter((child, childOrd) => childOrd == this.state.tabSelected)}
				</div>
			</div>
	}
};

export class Tab extends React.Component{

	render() {
		return <div className='s4lTabs'>
				{this.props.children}
			</div>
	}
};