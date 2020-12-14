using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RealtimeCodeEditor.Models.Entities
{
    public class Session
    {
        public string SessionCode { get; private set; }

        public SessionTypeEnum Type { get; set; }

        public string Creator { get; private set; }

        public string SavedState { get; set; }

        public DateTime LastActiveDateTime { get; set; }

        private ConcurrentBag<string> _users;

        public Session(string sessionCode, string creator)
        {
            SessionCode = sessionCode;
            Type = SessionTypeEnum.Active;
            Creator = creator;
            _users = new ConcurrentBag<string>();
            _users.Add(creator);
        }
        
        public void AddUser(string user)
        {
            if (Type == SessionTypeEnum.Expired)
            {
                throw new Exception("Attempts to update an expired session.");
            }

            _users.Add(user);
        }

        public void TouchSession()
        {
            if (Type == SessionTypeEnum.Expired)
            {
                throw new Exception("Attempts to touch an expired session.");
            }

            LastActiveDateTime = DateTime.UtcNow;
        }

        public void Expire()
        {
            Type = SessionTypeEnum.Expired;
        }

        public string[] GetUsers(string except = "")
        {
            if (except == "")
            {
                return _users.ToArray();
            } else
            {
                return _users.Where(s => s != except).ToArray();
            }
        }
    }
}
