import React, { useState } from 'react';

// properties
// value = value to display
// updateFunc = function to call when update
// blank = value to display if blank
const months = [ 'January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December' ];

const daysInMonths = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

export default function Calendar(props) {

    const [ calState, setCalState ] = useState({ editMode: false })

    const changeEditMode = (mode) => {
        if (mode != calState.editMode) {
            if (mode) {
                var dValue = new Date();
                if (props.value != '') {
                    dValue = new Date(props.value);
                }
                resetCal(dValue.getFullYear(), dValue.getMonth());
            }
            else {
                setCalState({ editMode: false});
            }
        }
    }

    const doSave = (day) => {
        if (day != '') {
            props.updateFunc([calState.cal_m+1, day, calState.cal_y].join('-'));
            setCalState({ editMode: false});
        }
    }

    const goNext = () => {
        if (calState.cal_m >= 11) {
            resetCal(calState.cal_y + 1, 0);
        }
        else {
            resetCal(calState.cal_y, calState.cal_m + 1);
        }
    }

    const goPrev = () => {
        if (calState.cal_m <= 0) {
            resetCal(calState.cal_y - 1, 11);
        }
        else {
            resetCal(calState.cal_y, calState.cal_m - 1);
        }
    }

    const resetCal = (cal_y, cal_m) => {

        var today = new Date();
        var dateValue = props.value == '' ? new Date() : new Date(props.value);

        var leadSpaces = new Date(cal_y, cal_m, 1).getDay();
        
        var daysInMonth = daysInMonths[cal_m];
        if (cal_m == 1) {
            if (((cal_y % 4 == 0) && (cal_y % 100 != 0)) || (cal_y % 400 == 0)) {
                daysInMonth++;
            }
        }

        var today_d = today.getDate()

        var val_style = (dateValue.getMonth() == cal_m && dateValue.getFullYear() == cal_y)
                ? { borderColor: '#000', border: '1px solid'}
                : {};

        var today_style = (today.getMonth() == cal_m && today.getFullYear() == cal_y)
                ? { fontWeight: 'bold' }
                : {};
        
        var weeks = [];
        var week = [];
        weeks.push(week);
        for (var i=0; i<leadSpaces; i++) {
            week.push({name: ''});
        }
        for (var j=1; j<=daysInMonth; j++) {
            var style = { width: '24px', textAlign: 'center' }
            if (j == dateValue.getDate()) {
                style = {...style, ...val_style};
            }
            if (j == today_d) {
                style = {...style, ...today_style};
            }
            week.push({name: j, style: style});
            if ((j+leadSpaces) % 7 == 0 ) {
                week = [];
                weeks.push(week);
            }
        }

        setCalState({ editMode: true, cal_y: cal_y, cal_m: cal_m, weeks: weeks });
    }

    const { value, blank } = props;
    const { editMode, cal_y, cal_m, weeks } = calState;

    return <span style={{cursor: 'pointer'}}  onClick={() => changeEditMode(true)}>
            {value == '' ? <span style={{color: 'gray'}}><i>{blank}</i></span> : value}
            {editMode 
                ? <span>
                        <table style={{position: 'absolute', background: '#fff', zIndex: 100, border: '2px solid #37b', borderSpacing: '0px', cursor: 'pointer'}}>
                            <tbody>
                                <tr style={{backgroundColor: '#37b', color: '#fff', textAlign: 'center'}}>
                                    <th onClick={() => goPrev()}>&#x25C4;</th>
                                    <th colSpan='5'>{months[cal_m] + ' ' + cal_y}</th>
                                    <th onClick={() => goNext()}>&#x25BA;</th>
                                </tr>
                                <tr style={{backgroundColor: '#37b', color: '#fff', textAlign: 'center'}}>
                                    <th>S</th>
                                    <th>M</th>
                                    <th>T</th>
                                    <th>W</th>
                                    <th>T</th>
                                    <th>F</th>
                                    <th>S</th>
                                </tr>
                                {weeks.map((week, weekOrd) => 
                                    <tr key={weekOrd}>
                                        {week.map((day, dayOrd) => 
                                            <td key={dayOrd} style={day.style}
                                                onMouseOver={(e) => e.target.style.background='yellow'}
                                                onMouseOut={(e) => e.target.style.background=''}
                                                onClick={() => doSave(day.name)}>
                                                {day.name}
                                            </td>
                                        )}
                                    </tr>
                                )}
                            </tbody>
                        </table>
                        <div style={{position: 'fixed', zIndex: 99, opacity: .3, background: '#aac', top: 0, left: 0, width: '100%', height: '100%'}}
                            onClick={() => changeEditMode(false)}>

                        </div>
                    </span>
                : <span></span>
            }
        </span>
}
