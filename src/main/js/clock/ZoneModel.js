import React, { useState } from 'react';

import ScrollDiv from '../components/ScrollDiv';
import useFetchPost from '../lib/useFetchPost';

export default function ZoneModal(props) {

    const { selectZoneFunc } = props;
    const [ zoneReq, setZoneReq ] = useState({cmd: 'get', path: ['']})
    const zoneReqStatus = useFetchPost('/clock/api/zoneReq', zoneReq, null);

    return (
        <>
            {zoneReqStatus.data != null && 
                zoneReqStatus.data.crumbs.map((crumb, ci) => 
                <div key={ci} className='flexCol' style={{padding: '5px', cursor: 'pointer'}}>
                    <ScrollDiv>
                        {crumb.subZones.map((subZ, i) => 
                            <div key={i}>
                                {subZ.parent
                                    ? <span onClick={() => setZoneReq({cmd: 'get', path: crumb.path.concat(subZ.name)})}>{subZ.name} &gt;</span>
                                    : <span onClick={() => selectZoneFunc(subZ.path)}>{subZ.name}</span>}
                            </div>)}
                    </ScrollDiv>
                </div>)}
        </>
    )
}