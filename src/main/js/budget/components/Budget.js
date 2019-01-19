import React from 'react';
import { connect } from "react-redux"
import Modal from 'react-modal';
import { getTransactions, postBudget } from "../actions/budgetActions"
import Calendar from "../../components/Calendar"
import FieldEdit from "../../components/FieldEdit"
import DropEdit from "../../components/DropEdit"
import { TabSet, Tab } from "../../components/TabSet"
import { formatCurrency } from "../../lib/s4lib"

Modal.setAppElement('#react');

class Budget extends React.Component{

    constructor(props) {
		super(props);
		
		this.dayValues = [];
		this.dayValues.push({value: 0, text: 'EOM'});
		for (var i=1; i<=31; i++) {
			this.dayValues.push({value: i, text: i});
		}
		this.dayValues.push({value: -1, text: 'Sunday'});
		this.dayValues.push({value: -2, text: 'Monday'});
		this.dayValues.push({value: -3, text: 'Tuesday'});
		this.dayValues.push({value: -4, text: 'Wednesday'});
		this.dayValues.push({value: -5, text: 'Thursday'});
		this.dayValues.push({value: -6, text: 'Friday'});
		this.dayValues.push({value: -7, text: 'Saturday'});

		this.nMonsValues = [];
		this.nMonsValues.push({value: 0, text: 'Disable'});	
		for (var i=1; i<=12; i++) {
			this.nMonsValues.push({value: i, text: i});	
		}

		this.specTypes = [{value: 'DBT', text: 'Debit'},
						{value: 'CRDT', text: 'Credit'}];
	}

    componentWillMount() {
		this.props.getTransactions('trans');
    }

	budgetHandler(name, value) {
		if (name.indexOf('t.value') == 0 || name.indexOf('ts.value') == 0) {
			while (value.indexOf(',') >= 0) {
				value = value.replace(',','')
			}
		}
		this.props.postBudget('budgetHandler', {name: name, value: value});
	}

	render() {
		const { transactions } = this.props.budgetR;

		if (transactions == null) {
			return <div>Loading...</div>
		}

		return <div>
			<div id='content'>
				<TabSet>
					<Tab name='Trans'>
						<table style={{borderCollapse: 'collapse'}}>
							<thead>
								<tr className='bordered'>
									<th style={{width: '120px', textAlign: 'center'}}>Date</th>
									<th style={{width: '250px'}}>Description</th>
									<th style={{width: ' 75px', textAlign: 'center'}}>Type</th>
									<th style={{width: ' 75px', textAlign: 'center'}}>State</th>
									<th style={{width: '200px', textAlign: 'right'}}>Debit</th>
									<th style={{width: '200px', textAlign: 'right'}}>Credit</th>
									<th style={{width: '100px', textAlign: 'right'}}>Balance</th>
								</tr>
							</thead>
							<tbody>
								<tr className='bordered'>
									<td style={{textAlign: 'center'}}><Calendar value='' blank='NEW' updateFunc={(date) => this.budgetHandler('newTran', date)} /></td>
									<td colSpan='6'></td>
								</tr>
								{transactions.trans.map((tran, tranOrd) => 
									<tr key={tranOrd} className={'bordered'} style={tran.month % 2 == 0 ? {backgroundColor: '#eef'} :  {}}>
										<td style={{textAlign: 'center'}}>{tran.state == 'Estimate' || tran.state == 'Cleared'
											? tran.date
											: <Calendar value={tran.date} blank='huh?' updateFunc={(date) => this.budgetHandler('t.date|' + tran.id, date)} /> }
										</td>
										<td>{tran.spec == null
											? <FieldEdit value={tran.description} blank='NEW' updateFunc={(value) => this.budgetHandler('t.description|'+tran.id, value)} />
											: tran.spec.description}</td>
										<td style={{textAlign: 'center'}}>{tran.spec == null
											? <DropEdit value={tran.type} options={this.specTypes} updateFunc={(value) => this.budgetHandler('t.type|'+tran.id, value)} />
											: tran.spec.type}</td>
										<td style={{textAlign: 'center'}}>{tran.state}</td>
										<td style={{textAlign: 'right'}}>{
											tran.type == 'DBT' && (tran.state == 'Proposed' || tran.state == 'Scheduled'
												? <FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => this.budgetHandler('t.value|'+tran.id, value)}/>
												: formatCurrency(tran.value))}</td>
										<td style={{textAlign: 'right'}}>{
											tran.type == 'CRDT' && (tran.state == 'Proposed' || tran.state == 'Scheduled'
												? <FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => this.budgetHandler('t.value|'+tran.id, value)}/>
												: formatCurrency(tran.value))}</td>
										<td style={{textAlign: 'right'}}>{formatCurrency(tran.subTotal)}</td>
										<td>
											{tran.firstGenerated &&
												<button onClick={() => this.budgetHandler('setTran', tran.spec.id + '|' + tran.date)}>Propose</button>}
											{tran.state == 'Proposed' &&
												<button onClick={() => this.budgetHandler('schedTran', tran.id)}>Schedule</button>}
											{tran.state == 'Scheduled' &&
												<span>
													<button onClick={() => this.budgetHandler('delTran', tran.id)}>Delete</button>
													<button onClick={() => this.budgetHandler('clrTran', tran.id)}>Cleared</button></span>}
										</td>
									</tr>)}
							</tbody>
						</table>
					</Tab>
					<Tab name='Tran Specs'>
						<table style={{borderCollapse: 'collapse'}}>
							<thead>
								<tr className='bordered'>
									<th style={{width: '250px'}}>Description</th>
									<th style={{width: '150px'}}>Num Mons</th>
									<th style={{width: '150px'}}>Day</th>
									<th style={{width: '150px'}}>Type</th>
									<th style={{width: '150px', textAlign: 'right'}}>Value</th>
								</tr>
							</thead>
							<tbody>
								{transactions.transSpecs.map((tran, tranOrd) => 
										<tr key={tranOrd} className={'bordered'} style={tranOrd % 2 == 0 ? {backgroundColor: '#eef'} :  {}}>
											<td><FieldEdit value={tran.description} blank='BLANK' updateFunc={(value) => this.budgetHandler('ts.description|'+tran.id, value)}/></td>
											<td><DropEdit value={tran.nmons} options={this.nMonsValues} updateFunc={(value) => this.budgetHandler('ts.nMons|'+tran.id, value)} /></td>
											<td><DropEdit value={tran.day} options={this.dayValues} updateFunc={(value) => this.budgetHandler('ts.day|'+tran.id, value)} /></td>
											<td><DropEdit value={tran.type} options={this.specTypes} updateFunc={(value) => this.budgetHandler('ts.type|'+tran.id, value)} /></td>
											<td style={{textAlign: 'right'}}><FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => this.budgetHandler('ts.value|'+tran.id, value)}/></td>
										</tr>)
								}
							</tbody>
						</table>
					</Tab>
					<Tab name='Tran History'>
						<button onClick={() => this.props.getTransactions('history')}>Load</button>
						<table style={{borderCollapse: 'collapse'}}>
							<thead>
								<tr className='bordered'>
									<th style={{width: '120px', textAlign: 'center'}}>Date</th>
									<th style={{width: '250px'}}>Description</th>
									<th style={{width: ' 75px', textAlign: 'center'}}>Type</th>
									<th style={{width: ' 75px', textAlign: 'center'}}>State</th>
									<th style={{width: '200px', textAlign: 'right'}}>Debit</th>
									<th style={{width: '200px', textAlign: 'right'}}>Credit</th>
									<th style={{width: '100px', textAlign: 'right'}}>Balance</th>
								</tr>
							</thead>
							<tbody>
								{transactions.transHist.map((tran, tranOrd) => 
									<tr key={tranOrd} className={'bordered'} style={tran.month % 2 == 0 ? {backgroundColor: '#eef'} :  {}}>
										<td style={{textAlign: 'center'}}>{tran.date.substring(0, 10)}</td>
										<td>{tran.description}</td>
										<td style={{textAlign: 'center'}}>{tran.type}</td>
										<td style={{textAlign: 'center'}}>{tran.state}</td>
										<td style={{textAlign: 'right'}}>{tran.type == 'DBT' ? formatCurrency(tran.value) : ''}</td>
										<td style={{textAlign: 'right'}}>{tran.type == 'CRDT' ? formatCurrency(tran.value) : ''}</td>
										<td style={{textAlign: 'right'}}>{formatCurrency(tran.subTotal)}</td>
									</tr>)}
							</tbody>
						</table>
					</Tab>
				</TabSet>
			</div>
		</div>
	}
};

const mapStateToProps = state => {
	return {
		budgetR: state.budgetReducer
	}
};

const mapDispatchToProps = dispatch => {
	return {
		getTransactions: (url) => {
			dispatch(getTransactions(url))
		},
		postBudget: (url, data) => {
			dispatch(postBudget(url, data))
		},
	}
};

export default connect(mapStateToProps, mapDispatchToProps)(Budget);