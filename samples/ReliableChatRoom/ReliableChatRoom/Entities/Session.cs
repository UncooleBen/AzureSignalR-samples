﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Entities
{
    public class Session
    {
        public string Username { get; set; }
        public string ConnectionId { get; set; }
        public string DeviceUuid { get; set; }
        public DateTime LastTouchedDateTime { get; set; }
        public SessionTypeEnum SessionType { get; set; }
        
        public Session(string username, string connectionId, string deviceUuid)
        {
            this.Username = username;
            this.ConnectionId = connectionId;
            this.DeviceUuid = deviceUuid;
            this.LastTouchedDateTime = DateTime.UtcNow;
            this.SessionType = SessionTypeEnum.Active;
        }

        public void Expire()
        {
            this.SessionType = SessionTypeEnum.Expired;
        }

        public void Revive(string connectionId, string deviceUuid)
        {
            this.SessionType = SessionTypeEnum.Active;
            this.ConnectionId = connectionId;
            this.DeviceUuid = deviceUuid;
        }

    }
}