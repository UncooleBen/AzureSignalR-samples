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
        public void OnEnterSession(string user)
        {
            _logger.LogInformation("OnEnterSession {0}", user);
            _sessionHandler.AddOrUpdateConnectionId(user, Context.ConnectionId);
            Clients.Client(Context.ConnectionId).SendAsync("enableEditor");
        }

        public void OnCodeEditorStateChanged(string sessionCode, string user, string content)
        {
            _logger.LogInformation("OnCodeEditorStateChanged");

            IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode, user);

            Clients.Clients(targetConnectionIds).SendAsync("updateCodeEditor", content);
        }

        public void OnCodeEditorLocked(string sessionCode, string user)
        {
            _logger.LogInformation("OnCodeEditorLocked");

            IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode, user);

            Clients.Clients(targetConnectionIds).SendAsync("lockCodeEditor");
        }

        public void OnCodeEditorUnlocked(string sessionCode, string user)
        {
            _logger.LogInformation("OnCodeEditorUnlocked");

            IReadOnlyList<string> targetConnectionIds = _sessionHandler.GetSessionConnectionIds(sessionCode, user);

            Clients.Clients(targetConnectionIds).SendAsync("unlockCodeEditor");
        }
    }
}
