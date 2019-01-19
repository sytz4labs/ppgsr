import React from 'react';

// properties
// value = value to display
// updateFunc = function to call when update
// blank = value to display if blank
export default class Calendar extends React.Component{

    constructor(props) {
        super(props);

        this.months = [ 'January', 'February', 'March',
        "April", "May", 'June', 'July', 'August', 'September', 'October',
        'November', 'December' ];

        this.daysInMonths = [ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ];

        this.state = { editMode: false };
    }

    setEditMode(mode) {
        if (mode != this.state.editMode) {
            if (mode) {
                var dValue = new Date();
                if (this.props.value != '') {
                    dValue = new Date(this.props.value);
                }
                this.resetCal(dValue.getFullYear(), dValue.getMonth());
            }
            else {
                this.setState({ editMode: false});
            }
        }
    }

    doSave(day) {

        if (day != '') {
            this.props.updateFunc([this.state.cal_m+1, day, this.state.cal_y].join('-'));//new Date(this.state.cal_y, this.state.cal_m, day));
            this.setState({ editMode: false});
        }
    }

    goNext() {
        if (this.state.cal_m >= 11) {
            this.resetCal(this.state.cal_y + 1, 0);
        }
        else {
            this.resetCal(this.state.cal_y, this.state.cal_m + 1);
        }
    }

    goPrev() {
        if (this.state.cal_m <= 0) {
            this.resetCal(this.state.cal_y - 1, 11);
        }
        else {
            this.resetCal(this.state.cal_y, this.state.cal_m - 1);
        }
    }

    resetCal(cal_y, cal_m) {

        var today = new Date();
        var dateValue = this.props.value == '' ? new Date() : new Date(this.props.value);

        var leadSpaces = new Date(cal_y, cal_m, 1).getDay();
        
        var daysInMonth = this.daysInMonths[cal_m];
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

        this.setState({ editMode: true, cal_y: cal_y, cal_m: cal_m, weeks: weeks });
    }

	render() {
        const { value, blank } = this.props;
        const { editMode, cal_y, cal_m, weeks } = this.state;

        return <span style={{cursor: 'pointer'}}  onClick={() => this.setEditMode(true)}>
                {value == '' ? <span style={{color: 'gray'}}><i>{blank}</i></span> : value}
                {editMode 
                    ? <span>
                            <table style={{position: 'absolute', background: '#fff', zIndex: 100, border: '2px solid #37b', borderSpacing: '0px', cursor: 'pointer'}}>
                                <tbody>
                                    <tr style={{backgroundColor: '#37b', color: '#fff', textAlign: 'center'}}>
                                        <th onClick={() => this.goPrev()}>&#x25C4;</th>
                                        <th colSpan='5'>{this.months[cal_m] + ' ' + cal_y}</th>
                                        <th onClick={() => this.goNext()}>&#x25BA;</th>
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
                                                    onClick={() => this.doSave(day.name)}>
                                                    {day.name}
                                                </td>
                                            )}
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                            <div style={{position: 'fixed', zIndex: 99, opacity: .3, background: '#aac', top: 0, left: 0, width: '100%', height: '100%'}}
                                onClick={() => this.setEditMode(false)}>

                            </div>
                        </span>
                    : <span></span>
                }
			</span>
	}
}
