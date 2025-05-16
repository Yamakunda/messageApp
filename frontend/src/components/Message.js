import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';

const Message = ({ message }) => {
  const { userId } = useContext(AuthContext);
  const isSentByUser = message.senderId == userId;

  return (
    <div
      className={`max-w-[60%] p-3 rounded-lg mb-2 ${
        isSentByUser
          ? 'bg-blue-600 text-white self-end'
          : 'bg-gray-200 text-gray-800 self-start'
      }`}
    >
      <p>{message.content}</p>
      {/* <span className="text-xs opacity-70">{message.status}</span> */}
    </div>
  );
};

export default Message;