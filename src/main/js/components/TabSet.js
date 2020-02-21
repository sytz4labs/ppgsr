import React, { useState } from 'react';

function tabChildFilter(child) {
	return React.isValidElement(child);
}

export function TabSet(props) {

	const [ tabSelected, setTabSelected ] = useState(0);

	return <div className='s4lTabs'>
			{props.children.filter(tabChildFilter).map((child, childOrd) =>
				<div key={childOrd} className={'s4lTab' + (childOrd == tabSelected ? ' s4lSelected' : '')}
					onClick={(e) => setTabSelected(childOrd)}>
					{child.props.name}
				</div>)}
			<div className='s4lTabsContent'>
				{props.children.filter(tabChildFilter).filter((child, childOrd) => childOrd == tabSelected)}
			</div>
		</div>
}

export function Tab(props) {

	return <div className='s4lTabs'>
			{props.children}
		</div>
}
