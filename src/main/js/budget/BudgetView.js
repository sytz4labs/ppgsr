import React, { useEffect } from 'react';
import Modal from 'react-modal';
import { budgetActs } from "./budgetActions"
import Calendar from "../components/Calendar"
import FieldEdit from "../components/FieldEdit"
import DropEdit from "../components/DropEdit"
import { TabSet, Tab } from "../components/TabSet"
import { formatCurrency } from "../lib/s4lib"
import { useBudgetReducer } from './budgetReducer';

Modal.setAppElement('#react');

export default function BudgetView(props) {

	const [ budgetState, dispatch ] = useBudgetReducer();
	useEffect(() => {
		getTransactions('trans')
	}, []);
	const { transactions } = budgetState;

	const getTransactions = (url) => {
		budgetActs.getTransactions(dispatch, url);
	};
	const postBudget = (url, data) => {
		budgetActs.postBudget(dispatch, url, data);
	};
	const budgetHandler = (name, value) => {
		if (name.indexOf('t.value') == 0 || name.indexOf('ts.value') == 0) {
			while (value.indexOf(',') >= 0) {
				value = value.replace(',','')
			}
		}
		postBudget('budgetHandler', {name: name, value: value});
	}

	var dayValues = [];
	dayValues.push({value: 0, text: 'EOM'});
	for (var i=1; i<=31; i++) {
		dayValues.push({value: i, text: i});
	}
	dayValues.push({value: -1, text: 'Sunday'});
	dayValues.push({value: -2, text: 'Monday'});
	dayValues.push({value: -3, text: 'Tuesday'});
	dayValues.push({value: -4, text: 'Wednesday'});
	dayValues.push({value: -5, text: 'Thursday'});
	dayValues.push({value: -6, text: 'Friday'});
	dayValues.push({value: -7, text: 'Saturday'});

	var nMonsValues = [];
	nMonsValues.push({value: 0, text: 'Disable'});	
	for (var i=1; i<=12; i++) {
		nMonsValues.push({value: i, text: i});	
	}

	const specTypes = [{value: 'DBT', text: 'Debit'},
					{value: 'CRDT', text: 'Credit'}];

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
								<th style={{width: ' 75px', textAlign: 'center'}}>State</th>
								<th style={{width: '275px'}}></th>
								<th style={{width: ' 75px', textAlign: 'center'}}>Type</th>
								<th style={{width: '200px', textAlign: 'right'}}>Debit</th>
								<th style={{width: '200px', textAlign: 'right'}}>Credit</th>
								<th style={{width: '100px', textAlign: 'right'}}>Balance</th>
							</tr>
						</thead>
						<tbody>
							<tr className='bordered'>
								<td style={{textAlign: 'center'}}><Calendar value='' blank='NEW' updateFunc={(date) => budgetHandler('newTran', date)} /></td>
								<td colSpan='6'></td>
							</tr>
							{transactions.trans.map((tran, tranOrd) => 
								<tr key={tranOrd} className={'bordered'} style={tran.month % 2 == 0 ? {backgroundColor: '#eef'} :  {}}>
									<td style={{textAlign: 'center'}}>{tran.state == 'Estimate' || tran.state == 'Cleared'
										? tran.date
										: <Calendar value={tran.date} blank='huh?' updateFunc={(date) => budgetHandler('t.date|' + tran.id, date)} /> }
									</td>
									<td>{tran.spec == null
										? <FieldEdit value={tran.description} blank='NEW' updateFunc={(value) => budgetHandler('t.description|'+tran.id, value)} />
										: tran.spec.description}</td>
									<td style={{textAlign: 'center'}}>{tran.state}</td>
									<td>
										{tran.firstGenerated &&
											<button onClick={() => budgetHandler('setTran', tran.spec.id + '|' + tran.date)}>Propose</button>}
										{tran.state == 'Proposed' &&
											<button onClick={() => budgetHandler('schedTran', tran.id)}>Schedule</button>}
										{tran.state == 'Scheduled' &&
											<span>
												<button onClick={() => budgetHandler('delTran', tran.id)}>Delete</button>
												<button onClick={() => budgetHandler('clrTran', tran.id)}>Cleared</button></span>}
									</td>
									<td style={{textAlign: 'center'}}>{tran.spec == null
										? <DropEdit value={tran.type} options={specTypes} updateFunc={(value) => budgetHandler('t.type|'+tran.id, value)} />
										: tran.spec.type}</td>
									<td style={{textAlign: 'right'}}>{
										tran.type == 'DBT' && (tran.state == 'Proposed' || tran.state == 'Scheduled'
											? <FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => budgetHandler('t.value|'+tran.id, value)}/>
											: formatCurrency(tran.value))}</td>
									<td style={{textAlign: 'right'}}>{
										tran.type == 'CRDT' && (tran.state == 'Proposed' || tran.state == 'Scheduled'
											? <FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => budgetHandler('t.value|'+tran.id, value)}/>
											: formatCurrency(tran.value))}</td>
									<td style={{textAlign: 'right'}}>{formatCurrency(tran.subTotal)}</td>
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
										<td><FieldEdit value={tran.description} blank='BLANK' updateFunc={(value) => budgetHandler('ts.description|'+tran.id, value)}/></td>
										<td><DropEdit value={tran.nmons} options={nMonsValues} updateFunc={(value) => budgetHandler('ts.nMons|'+tran.id, value)} /></td>
										<td><DropEdit value={tran.day} options={dayValues} updateFunc={(value) => budgetHandler('ts.day|'+tran.id, value)} /></td>
										<td><DropEdit value={tran.type} options={specTypes} updateFunc={(value) => budgetHandler('ts.type|'+tran.id, value)} /></td>
										<td style={{textAlign: 'right'}}><FieldEdit value={formatCurrency(tran.value)} blank='BLANK' updateFunc={(value) => budgetHandler('ts.value|'+tran.id, value)}/></td>
									</tr>)
							}
							<tr>
								<td><FieldEdit value='' blank='NEW' updateFunc={(value) => budgetHandler('addTranSpec', value)}/></td>
								<td colSpan='5'></td>
							</tr>
						</tbody>
					</table>
				</Tab>
				<Tab name='Tran History'>
					<button onClick={() => getTransactions('history')}>Load</button>
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