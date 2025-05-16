import React, { useState, useEffect, useContext, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';
import Message from '../components/Message';
import UserList from '../components/UserList';

const Chat = () => {
  const { token, userId } = useContext(AuthContext);
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [recipientId, setRecipientId] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [client, setClient] = useState(null);
  const [users, setUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState(null);
  const chatWindowRef = useRef(null); // Reference to chat container

  // Fetch users
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/account/all', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUsers(response.data);
      } catch (err) {
        console.error('Failed to fetch users:', err);
      }
    };
    fetchUsers();
  }, [token]);

  // Fetch chat history when recipientId changes
  useEffect(() => {
    if (!selectedUserId) return;

    const fetchHistory = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/api/message/history/${selectedUserId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setMessages(response.data);
      } catch (err) {
        console.error('Failed to fetch chat history:', err);
      }
    };
    fetchHistory();
  }, [selectedUserId, token]);
  useEffect(() => {
    if (chatWindowRef.current) {
      chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight;
    }
  }, [messages]);
  // WebSocket setup
  useEffect(() => {
    if (!token || !userId) {
      console.error('Missing token or userId');
      return;
    }

    const authHeader = `Bearer ${token}`;
    console.log('WebSocket connectHeaders:', { Authorization: authHeader });

    const stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: { Authorization: authHeader },
      onConnect: (frame) => {
        console.log('WebSocket connected:', frame);
        stompClient.subscribe(`/user/${userId}/queue/messages`, (msg) => {
          const message = JSON.parse(msg.body);
          setMessages((prev) => [...prev, message]);
          axios.post(
            'http://localhost:8080/api/message/messages/seen',
            { messageId: message.messageId },
            { headers: { Authorization: authHeader } }
          ).catch((err) => console.error('Failed to mark seen:', err));
        });
        stompClient.subscribe(`/user/${userId}/queue/typing`, (msg) => {
          const { senderId, isTyping } = JSON.parse(msg.body);
          if (senderId === selectedUserId) setIsTyping(isTyping);
        });
        stompClient.subscribe(`/user/${userId}/queue/seen`, (msg) => {
          const { messageId, status } = JSON.parse(msg.body);
          setMessages((prev) =>
            prev.map((m) => (m.messageId === messageId ? { ...m, status } : m))
          );
        });
      },
    });

    stompClient.activate();
    setClient(stompClient);
    return () => {
      console.log('Disconnecting WebSocket');
      stompClient.deactivate();
    };
  }, [token, userId, selectedUserId]);

  const sendMessage = async () => {
    if (!message || !selectedUserId) return;
    try {
      const response = await axios.post(
        'http://localhost:8080/api/message/messages',
        { recipientId: selectedUserId, content: message },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setMessages((prev) => [...prev, response.data]);
      setMessage('');
    } catch (err) {
      console.error('Failed to send message:', err);
    }
  };

  const handleTyping = async (e) => {
    setMessage(e.target.value);
    try {
      await axios.post(
        'http://localhost:8080/api/message/typing',
        { recipientId: selectedUserId, isTyping: e.target.value.length > 0 },
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (err) {
      console.error('Typing error:', err);
    }
  };

  const handleSelectUser = (selectId) => {
    setSelectedUserId(selectId);
    setRecipientId(selectId);
    console.log('Selected user ID:', selectId);
  };

  const handleRecipientInput = (e) => {
    setRecipientId(e.target.value);
    setSelectedUserId(e.target.value ? parseInt(e.target.value) : null);
  };

  return (
    <div className="flex max-w-4xl mx-auto mt-6 h-[600px]">
      <UserList users={users} onSelectUser={handleSelectUser} currentUserId={userId} />
      <div className="flex-1 p-4 bg-white rounded-lg shadow-md">
        <h2 className="text-xl font-bold mb-4">Chat</h2>
        {/* <div className="mb-4">
          <label className="block text-sm font-medium text-gray-700">Recipient ID</label>
          <input
            type="text"
            value={recipientId}
            onChange={handleRecipientInput}
            placeholder="Enter recipient ID"
            className="mt-1 w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div> */}
        <div ref={chatWindowRef} className="h-[460px] overflow-y-auto border border-gray-200 p-4 rounded-md flex flex-col">
          {isTyping && (
            <p className="text-sm italic text-gray-500 mb-2">Recipient is typing...</p>
          )}
          {messages.map((msg) => (
            <Message key={msg.messageId} message={msg} />
          ))}
        </div>
        <div className="mt-4">
          <form
            className="flex gap-2"
            onSubmit={(e) => {
              e.preventDefault();
              sendMessage();
            }}
          >
            <input
              type="text"
              value={message}
              onChange={handleTyping}
              placeholder="Type a message"
              className="flex-1 p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              disabled={!selectedUserId}
            />
            <button
              type="submit"
              className="py-2 px-4 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition disabled:opacity-50"
              disabled={!selectedUserId}
            >
              Send
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Chat;