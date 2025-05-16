import React from 'react';

const UserList = ({ users, onSelectUser, currentUserId }) => {
  return (
    <div className="w-full md:w-1/4 bg-gray-100 p-4 border-r border-gray-200">
      <h3 className="text-lg font-semibold mb-4">Users</h3>
      <ul className="space-y-2">
        {users
          .filter((user) => user.accountId != currentUserId) // Exclude current user
          .map((user) => (
            <li
              key={user.accountId}
              onClick={() => onSelectUser(user.accountId)}
              className="p-2 rounded-md hover:bg-blue-100 cursor-pointer"
            >
              {user.username} (ID: {user.accountId})
            </li>
          ))}
      </ul>
    </div>
  );
};
export default UserList;