import React, { useState } from 'react'; 
import FieldEdit from '../components/FieldEdit';
import useFetchPost from '../lib/useFetchPost';

export default function TasksView() {

    const [ req, setReq ] = useState({cmd: 'get'})
    const [ showCompleted, setShowCompleted ] = useState(false)
    const taskStatus = useFetchPost('/tasks/get', req, null);

    return <div id="content" style={{backgroundColor: 'white'}}>
                    { taskStatus.data == null
                        ? 'Loading. . .'
                        : <>
                            <input type='checkbox' onChange={e => setShowCompleted(!showCompleted)} checked={showCompleted} />Show Completed
                            <table style={{borderCollapse: 'collapse'}}>
                            <thead>
                                <tr className='bordered'>
                                    <th style={{width: '100px'}}>Area</th>
                                    <th style={{width: ' 75px'}}>Priority</th>
                                    <th style={{width: '700px'}}>Task</th>
                                    <th style={{width: '700px'}}>Notes</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr className={'bordered'}>
                                    <td></td>
                                    <td></td>
                                    <td><FieldEdit value='' updateFunc={(value) => {setReq({cmd: 'new', val: value})}} blank='NEW_TASK'/></td>
                                    <td></td>
                                </tr>
                                {taskStatus.data.map((task, i) => 
                                    (showCompleted || task.priority >= 0) &&
                                        <tr key={task.id} className={'bordered' + (i%2==0 ? ' tbl-odd' : '')}>
                                            <td style={{textAlign: 'center', verticalAlign: 'top'}}><FieldEdit value={task.area} updateFunc={(value) => {setReq({cmd: 'area', id: task.id, val: value})}} blank='BLANK'/></td>
                                            <td style={{textAlign: 'center', verticalAlign: 'top'}}><FieldEdit value={task.priority.toString()} updateFunc={(value) => {setReq({cmd: 'priority', id: task.id, val: value})}} blank='BLANK' size='5'/></td>
                                            <td style={{verticalAlign: 'top'}}><FieldEdit value={task.task} updateFunc={(value) => {setReq({cmd: 'task', id: task.id, val: value})}} blank='BLANK'/></td>
                                            <td style={{verticalAlign: 'top'}}><FieldEdit value={task.benefit} updateFunc={(value) => {setReq({cmd: 'benefit', id: task.id, val: value})}} blank='BLANK'/></td>
                                        </tr>)
                                    }
                            </tbody>
                        </table>
                        </>
                    }
    </div>;
}