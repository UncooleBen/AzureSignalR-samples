using Microsoft.AspNetCore.SignalR;
using Microsoft.Extensions.Logging;
using RealtimeCodeEditor.Models;
using RealtimeCodeEditor.Models.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace RealtimeCodeEditor.Hubs
{
    public class CodeEditorHub : Hub
    {
        private readonly ILogger<CodeEditorHub> _logger;
        private readonly SessionHandler _sessionHandler;

        public CodeEditorHub(ILogger<CodeEditorHub> logger, SessionHandler sessionHandler)
        {
            _logger = logger;
            _sessionHandler = sessionHandler;
        }

        private bool CheckSessionState(string sessionCode, string user)
        {
            if (!_sessionHandler.IsLegalUser(sessionCode, user)) {
                Clients.Client(Context.ConnectionId).SendAsync("expireSession");
                return false;
            }

            return true;
        }

        public void OnEnterSession(string sessionCode, string user)
        {
            if (CheckSessionState(sessionCode, user)) {
                _logger.LogInformation("OnEnterSession code: {0}, user: {1}", sessionCode, user);
                _sessionHandler.AddOrUpdateConnectionId(user, Context.ConnectionId);
                Clients.Client(Context.ConnectionId).SendAsync("enableEditor");
            }
        }

        public void OnCodeEditorStateChanged(string sessionCode, string user, string content)
        {
            if (CheckSessionState(sessionCode, user))
            {
                _logger.LogInformation("OnCodeEditorStateChanged");
                _sessionHandler.UpdateSessionState(sessionCode, content);
                IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode, user);

                if (targetConnectionIds.Count > 0)
                {
                    Clients.Clients(targetConnectionIds).SendAsync("updateCodeEditor", content);
                }
            }
        }

        public void OnCodeEditorLocked(string sessionCode, string user)
        {
            if (CheckSessionState(sessionCode, user))
            {
                if (!_sessionHandler.IsLegalCreator(sessionCode, user))
                {
                    return;
                }

                _logger.LogInformation("OnCodeEditorLocked");
                _sessionHandler.LockSession(sessionCode);

                IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode);
                if (targetConnectionIds.Count > 0)
                {
                    Clients.Clients(targetConnectionIds).SendAsync("lockCodeEditor");
                }
            }
        }

        public void OnCodeEditorUnlocked(string sessionCode, string user)
        {
            if (CheckSessionState(sessionCode, user))
            {
                if (!_sessionHandler.IsLegalCreator(sessionCode, user))
                {
                    return;
                }

                _logger.LogInformation("OnCodeEditorUnlocked");
                _sessionHandler.UnlockSession(sessionCode);

                IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode);
                if (targetConnectionIds.Count > 0)
                {
                    Clients.Clients(targetConnectionIds).SendAsync("unlockCodeEditor");
                }
            }
        }
    }
}
