﻿using Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Storage
{
    public interface IMessageStorage
    {
        bool TryStoreMessage(Message message);
        List<Message> GetHistoryMessage(string username, DateTime endDateTime);
    }
}