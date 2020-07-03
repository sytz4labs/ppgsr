import React, { useState, useRef, useEffect } from 'react';

// properties
// multiLine = boolean
// value = value to display
// updateFunc = function to call when update
// blank = value to display if blank
// size = number of characters
// clazz = ignore for now

export default function FieldEdit(props) {
    const { value = '', blank = 'BLANK', multiLine = false, size } = props;
    const [ editMode, setEditMode ] = useState(false);
    const textInput = useRef();
    useEffect(() => {
        if (editMode) {
            textInput.current.value = value;
            textInput.current.focus();
        }
    });

    const saveValue = () => {
        props.updateFunc(textInput.current.value)
        setEditMode(false);
    }

    const keyDown = (e) => {
        if (!multiLine && e.keyCode == 13) { // enter
            saveValue();
        }
        keyDownEsc(e);
    }

    const keyDownEsc = (e) => {
        if (e.keyCode == 27) { // escape
            setEditMode(false);
        }
    }

    return <span>
            { editMode
                ? (multiLine
                    ? <span><textarea onKeyDown={keyDownEsc} ref={textInput} style={{width: '900px', height: '300px', fontFamily: 'monospace'}}></textarea>
                        <span onClick={() => saveValue()} style={{color: 'green', cursor: 'pointer', fontSize: '30px'}}>&#x2714;</span>
                        <span onClick={() => setEditMode(false)} style={{color: 'red', cursor: 'pointer', fontSize: '30px'}}>&#x2718;</span>
                    </span> 
                    : <input onKeyDown={keyDown} onBlur={() => setEditMode(false)} ref={textInput} type='text' size={size} />)
                : <span  onClick={() => setEditMode(true)}>
                    {multiLine
                        ? <pre style={{border: '1px dashed #00d'}}>{value == '' ? <span style={{color: '#ddd'}}><i>{blank}</i></span> : value}</pre>
                        : <span style={{borderBottom: '1px dashed #00d'}}>{value == '' ? <span style={{color: '#ddd'}}><i>{blank}</i></span> : value} </span>
                    }
                    </span> }
        </span>
}