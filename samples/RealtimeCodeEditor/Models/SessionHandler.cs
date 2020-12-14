using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.Logging;
using RealtimeCodeEditor.Hubs;
using RealtimeCodeEditor.Models.Entities;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;

namespace RealtimeCodeEditor.Models
{
    public class SessionHandler
    {
        private static readonly string chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        private static readonly Random random = new Random();

        private ILogger<SessionHandler> _logger;

        private ConcurrentDictionary<string, Session> _sessions;
        private ConcurrentDictionary<string, string> _connectionIds;

        private readonly int _sessionCodeLength = 6;

        private readonly TimeSpan _sessionExpireThreshold = TimeSpan.FromSeconds(100);
        private readonly TimeSpan _sessionCheckingInterval = TimeSpan.FromSeconds(100);
        private Timer _sessionChecker;

        public SessionHandler(ILogger<SessionHandler> logger)
        {
            _logger = logger;
            _sessions = new ConcurrentDictionary<string, Session>();
            _connectionIds = new ConcurrentDictionary<string, string>();
            //_sessionChecker = new Timer(_ => CheckSession(), state: null, dueTime: TimeSpan.FromMilliseconds(0), period: _sessionCheckingInterval);
        }

        private string GenerateRandomSessionCode()
        {
            return new string(Enumerable.Repeat(chars, _sessionCodeLength)
                .Select(s => s[random.Next(s.Length)]).ToArray());
        }

        public string CreateSession(string creator)
        {
            string sessionCode = GenerateRandomSessionCode();

            Session session = new Session(sessionCode, creator);
            _sessions.TryAdd(sessionCode, session);

            _logger.LogInformation("Create Session creator: {0}, code: {1}", creator, sessionCode);
            return sessionCode;
        }

        public bool JoinSession(string sessionCode, string user)
        {
            bool sessionExists = _sessions.TryGetValue(sessionCode, out Session session);

            if (sessionExists)
            {
                session.AddUser(user);
                _logger.LogInformation("Join Session user: {0}, code: {1}", user, sessionCode);
                return true;
            }

            return false;
        }

        public Session GetSessionByCode(string sessionCode)
        {
            _sessions.TryGetValue(sessionCode, out Session session);
            return session;
        }

        public IReadOnlyList<string> GetSessionConnectionIds(string sessionCode, string except = "")
        {
            Session session = _sessions
                                .Where((pair) => pair.Key == sessionCode && pair.Value.Type == SessionTypeEnum.Active)
                                .First().Value;

            IEnumerable<string> connectionIds = from pair in _connectionIds
                                                where session.GetUsers(except).Contains(pair.Key)
                                                select pair.Key;

            return connectionIds.ToList().AsReadOnly();
        }

        public void AddOrUpdateConnectionId(string user, string connectionId)
        {
            _connectionIds.AddOrUpdate(user, (s) => connectionId, (s1, s2) => connectionId);
        }

        private void CheckSession()
        {
            foreach (var pair in _sessions)
            {
                Session session = pair.Value;
                if (session.Type == SessionTypeEnum.Active)
                {
                    var elapsed = DateTime.UtcNow - session.LastActiveDateTime;
                    if (elapsed > _sessionExpireThreshold)
                    {
                        _logger.LogInformation("Session sessCode: {0} time out. Force expire. Creator: {1}", session.SessionCode, session.Creator);
                        session.Expire();
                    }
                }
            }
        }
    }
}
