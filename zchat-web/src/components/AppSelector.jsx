import React from 'react';
import { MessageCircle, Instagram, Send, Video } from 'lucide-react';

export default function AppSelector({ onSelect }) {
  const apps = [
    { id: 'instagram', name: 'Instagram', icon: <Instagram size={32} />, color: 'bg-gradient-to-tr from-yellow-400 via-pink-500 to-purple-600' },
    { id: 'whatsapp', name: 'WhatsApp', icon: <MessageCircle size={32} />, color: 'bg-green-500' },
    { id: 'telegram', name: 'Telegram', icon: <Send size={32} />, color: 'bg-blue-500' },
    { id: 'tiktok', name: 'TikTok', icon: <Video size={32} />, color: 'bg-black border border-gray-700' },
  ];

  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-4 text-center">
      <h1 className="text-4xl font-bold mb-2">Welcome to zChat</h1>
      <p className="text-gray-400 mb-8">Select an app to test the AI integration</p>
      
      <div className="grid grid-cols-2 gap-4 md:grid-cols-4 max-w-2xl w-full">
        {apps.map((app) => (
          <button
            key={app.id}
            onClick={() => onSelect(app.id)}
            className={`${app.color} text-white p-6 rounded-2xl flex flex-col items-center justify-center gap-3 transition-transform hover:scale-105 shadow-xl`}
          >
            {app.icon}
            <span className="font-semibold">{app.name}</span>
          </button>
        ))}
      </div>
    </div>
  );
}
