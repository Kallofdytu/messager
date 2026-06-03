import React, { useState } from 'react';
import AppSelector from './components/AppSelector';
import ChatInterface from './components/ChatInterface';
import './index.css';

function App() {
  const [selectedApp, setSelectedApp] = useState(null);

  return (
    <div className="min-h-screen bg-slate-900 text-white font-sans">
      {!selectedApp ? (
        <AppSelector onSelect={setSelectedApp} />
      ) : (
        <ChatInterface appId={selectedApp} onBack={() => setSelectedApp(null)} />
      )}
    </div>
  );
}

export default App;
