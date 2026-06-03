import React, { useState } from 'react';
import { ChevronLeft, MoreVertical, Sparkles, Send } from 'lucide-react';

export default function ChatInterface({ appId, onBack }) {
  const [inputText, setInputText] = useState('');
  const [messages, setMessages] = useState([
    { id: 1, text: "Hey! Are you free this weekend?", isMe: false },
    { id: 2, text: "We're planning to go to the mountains.", isMe: false }
  ]);
  const [aiState, setAiState] = useState('idle'); // idle, thinking, ready

  const config = {
    instagram: { name: 'Instagram', headerBg: '#000', chatBg: '#000', myMsg: '#3797f0', theirMsg: '#262626', contact: 'alex_smith' },
    whatsapp: { name: 'WhatsApp', headerBg: '#075e54', chatBg: '#ece5dd', myMsg: '#dcf8c6', theirMsg: '#fff', contact: 'Alex' },
    telegram: { name: 'Telegram', headerBg: '#17212b', chatBg: '#0e1621', myMsg: '#2b5278', theirMsg: '#182533', contact: 'Alex' },
    tiktok: { name: 'TikTok', headerBg: '#000', chatBg: '#000', myMsg: '#fe2c55', theirMsg: '#252525', contact: 'alex123' },
  }[appId] || config.instagram;

  const handleAiClick = () => {
    if (aiState !== 'idle') return;
    setAiState('thinking');
    
    // Simulate AI processing
    setTimeout(() => {
      setInputText("That sounds amazing! I'd love to join you guys. What time are you heading out?");
      setAiState('ready');
    }, 2000);
  };

  const handleSend = () => {
    if (!inputText.trim()) return;
    setMessages([...messages, { id: Date.now(), text: inputText, isMe: true }]);
    setInputText('');
    setAiState('idle');
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-slate-900 p-4">
      <div 
        className="w-full max-w-sm rounded-3xl overflow-hidden shadow-2xl border-4 border-slate-800 flex flex-col relative"
        style={{ height: '600px', backgroundColor: config.chatBg }}
      >
        
        {/* Header */}
        <div className="flex items-center justify-between p-4 text-white" style={{ backgroundColor: config.headerBg }}>
          <div className="flex items-center gap-3">
            <button onClick={onBack} className="hover:opacity-70"><ChevronLeft /></button>
            <div className="flex flex-col">
              <span className="font-semibold">{config.contact}</span>
              <span className="text-xs opacity-70">Active now</span>
            </div>
          </div>
          <MoreVertical size={20} />
        </div>

        {/* Chat Area */}
        <div className="flex-1 overflow-y-auto p-4 flex flex-col gap-3">
          {messages.map(msg => (
            <div 
              key={msg.id} 
              className={`max-w-[80%] p-3 rounded-2xl ${msg.isMe ? 'self-end' : 'self-start'}`}
              style={{ 
                backgroundColor: msg.isMe ? config.myMsg : config.theirMsg,
                color: (appId === 'whatsapp' && msg.isMe) ? '#000' : '#fff',
                borderBottomRightRadius: msg.isMe ? '4px' : '16px',
                borderBottomLeftRadius: !msg.isMe ? '4px' : '16px'
              }}
            >
              {msg.text}
            </div>
          ))}
        </div>

        {/* AI Overlay */}
        {aiState === 'thinking' && (
          <div className="absolute bottom-16 left-0 w-full bg-slate-900/95 backdrop-blur-md text-white p-4 flex items-center justify-center gap-3 rounded-t-2xl shadow-[0_-10px_40px_rgba(0,0,0,0.5)] border-t border-slate-700 animate-slide-up">
            <Sparkles className="animate-pulse text-purple-400" />
            <span className="font-medium bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
              zChat AI is analyzing context...
            </span>
          </div>
        )}

        {/* Input Area */}
        <div className="p-3 bg-black flex items-center gap-2 border-t border-slate-800">
          <button 
            onClick={handleAiClick}
            className={`p-2 rounded-full flex-shrink-0 transition-all ${aiState === 'thinking' ? 'bg-slate-700 animate-pulse' : 'bg-gradient-to-tr from-blue-500 to-purple-600 hover:scale-110'}`}
          >
            <Sparkles size={20} color="white" />
          </button>
          
          <input 
            type="text" 
            value={inputText}
            onChange={(e) => {
              setInputText(e.target.value);
              setAiState('idle');
            }}
            placeholder="Message..."
            className="flex-1 bg-slate-800 text-white rounded-full px-4 py-2 outline-none focus:ring-1 focus:ring-slate-600"
          />
          
          <button onClick={handleSend} className="p-2 text-blue-500 hover:text-blue-400">
            <Send size={20} />
          </button>
        </div>

      </div>
    </div>
  );
}
