import React from 'react';
import { createRoot } from 'react-dom/client';
import QrSend from './QrSend';
import WebSocketProvider from '../lib/useWebSocketProvider';

createRoot(document.getElementById('react')).render(
  <WebSocketProvider uri='/qrsws'>
    <QrSend/>
  </WebSocketProvider>
)
