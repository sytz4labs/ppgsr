import React, { useContext, useState } from 'react';

import { useLoginDialog } from "../login/LoginDialog"

/* Properties
	 title
	 home
*/

const AppHeaderContext = React.createContext();

export default function AppHeader(props) {
	const loginContext = useLoginDialog();
	const [ affirm, setAffirm ] = useState(null);  // fix later

	const appHeaderContext = {
        setAffirm: (title, text) => {
			setAffirm({title, text})
		}
	};
		
	return <AppHeaderContext.Provider value={appHeaderContext}>
		<div>
			<div id="headerText" style={{cursor: "pointer"}} onClick={() => {top.location=props.home}}>{props.title}</div>
			<div                 style={{position: 'absolute', top: '18px', left: '120px', fontSize: '16px', lineHeight: '16px'}}><b>{affirm == null ? '' : affirm.title}</b> <span>{affirm == null ? '' : affirm.text}</span></div>

			<div id="headline">&nbsp;</div>
			<div id="headerLogin" style={{position: 'absolute', top: '0px', right: '10px'}}>{loginContext.state.userInfo == null
				? <button onClick={() => {loginContext.signIn()}}>Sign In</button>
				: <span>{loginContext.state.userInfo.userId} <button onClick={() => loginContext.logout()} >logout</button></span>}
			</div>
		</div>
		{props.children}
	</AppHeaderContext.Provider>
}

export const useAppHeader = () => {
    const appHeaderContext = useContext(AppHeaderContext);
    return { setAffirm: appHeaderContext.setAffirm };
}