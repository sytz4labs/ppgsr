import React, { useState } from 'react';

// properties
// value = value to display
// updateFunc(value) = function to call when update
// options = value to display if blank array of value: and text:
export default function DropEdit(props) {

    const [ editMode, setEditMode ] =useState(false);

    const handleInputChange = (event) => {
		const target = event.target;
        props.updateFunc(target.value);
		setEditMode(false);
	}

    const { value, options } = props;
    
    const display = options.filter((opt) => opt.value == value)[0].text;

    return <span>
            { editMode
                ? <select value={props.value} style={{cursor: 'pointer'}} onChange={(e) => handleInputChange(e)} onBlur={() => setEditMode(false)}>
                    {options.map((opt, optOrd) => 
                        <option key={optOrd} value={opt.value}>{opt.text}</option>)}
                </select>
                : <span style={{cursor: 'pointer'}} onClick={() => setEditMode(true)}>{display}</span>}
        </span>
        
}
