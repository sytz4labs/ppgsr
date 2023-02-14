import React from 'react';

// properties
// value = value to display
// updateFunc(value) = function to call when update
// options = value to display if blank array of value: and text:
export default function DropEdit(props) {

    const handleInputChange = (event) => {
		const target = event.target;
        props.updateFunc(target.value);
	}

    const { value, options } = props;
    console.log(value)
    console.log(JSON.stringify(options))    

    return <span>
            <select value={props.value} style={{cursor: 'pointer'}} onChange={(e) => handleInputChange(e)}>
                {options.map((opt, optOrd) => 
                    <option key={optOrd} value={opt.value}>{opt.text}</option>)}
            </select>
        </span>
        
}
