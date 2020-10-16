﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Handlers
{
    public interface IUserHandler
    {
        (string, string) Login(string username, string connectionId, string deviceToken);
        void Logout(string username);
        string GetUserConnectionId(string username);
        string GetUserDeviceToken(string username);
    }
}